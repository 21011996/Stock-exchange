package network

import messages.Message
import messages.Record
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.ConnectException
import java.net.ServerSocket
import java.net.Socket
import java.util.*

/**
 * Created by kirill on 03.04.17.
 */
class FixedAddressesNetworkLogicImpl private constructor(val nodeName: String, val myAddr: MyAddr,
                                                         val others: List<MyAddr>) : AbstractNetworkLogic() {
    private val serverSocket = ServerSocket(myAddr.port)

    init {
        println("$nodeName started...")
        startServerSocket()
        trySendHello()
    }

    fun startServerSocket() {
        Thread({
            while (!Thread.interrupted()) {
                try {
                    val socket = serverSocket.accept()
                    println("$socket accepted")
                    handleSocket(socket)
                } catch (e: IOException) {
                    println("I/O error on serverSocket: " + e)
                    serverSocket.close()
                    break
                }
            }
        }).start()
    }

    fun handleSocket(socket: Socket) {
        Thread({
            val input = BufferedReader(InputStreamReader(socket.getInputStream()))
            val output = DataOutputStream(socket.getOutputStream())

            var line: String?
            println("start tcp read cycle for $socket")
            while (!Thread.interrupted()) {
                try {
                    line = input.readLine()
                    if (line.startsWith(HELLO_PREFIX)) {
                        val name = line.split(' ')[1]
                        addressBook[name] = socket
                        output.writeBytes("$RESPONSE_PREFIX $nodeName\n")
                        output.flush()
                    } else if (line.startsWith(RESPONSE_PREFIX)) {
                        val name = line.split(' ')[1]
                        if (!addressBook.containsKey(name)) {
                            addressBook[name] = socket
                        }
                    } else {
                        
                        //TODO : read record here
                        val byteValues = line.substring(1, line.length - 1).split(",")
                        val bytes = ByteArray(byteValues.size)
                        var i = 0
                        val len = bytes.size
                        while (i < len) {
                            bytes[i] = java.lang.Byte.parseByte(byteValues[i].trim({ it <= ' ' }))
                            i++
                        }
                        //val msg = Message.parseRecord(Record.fromByteBuffer(bytes))
                        //TODO get msg somewhere
                    }
                    println("get $line from: $socket")
                } catch (e: Exception) {
                    println("tcp reader thread error: $e")
                    addressBook.values.remove(socket)
                    socket.close()
                    break
                }
            }
        }).start()
    }

    fun trySendHello() {
        for ((host, port) in others) {
            try {
                val socket = Socket(host, port)
                val output = DataOutputStream(socket.getOutputStream())
                output.writeBytes("$HELLO_PREFIX $nodeName\n")
                output.flush()
                println("connected to $socket")
                handleSocket(socket)
            } catch (ignored: ConnectException) { }
        }
    }

    override fun send(node: String, message: Message) {
        sendToSocket(message, addressBook[node]?: throw NoSuchNodeException(node))
    }

    override fun sendToSocket(message: Message, socket: Socket) {
        val output = DataOutputStream(socket.getOutputStream())
        output.write(message.toRecord().toByteArray())
        output.flush()
    }

    companion object {
        fun buildFromConfig(nodeName: String): FixedAddressesNetworkLogicImpl {
            val cfg = readConfig()
            return FixedAddressesNetworkLogicImpl(nodeName, MyAddr.fromConfig(nodeName, cfg), othersAddrs(nodeName, cfg))
        }

        data class Config(val name: String, val host: String, val port: Int)

        data class MyAddr(val host: String, val port: Int) {
            companion object {
                fun fromConfig(nodeName: String, config: List<Config>): MyAddr {
                    val cfg = config.filter { it.name == nodeName }.first()
                    return MyAddr(cfg.host, cfg.port)
                }
            }
        }

        private fun readConfig(): List<Config> {
            val confIS = FixedAddressesNetworkLogic2::class.java.getResourceAsStream("/addresses.conf")
            val lines = Scanner(confIS).useDelimiter("\\A").next().split('\n')
            return lines.map { line ->
                val (name, address) = line.split(' ')
                val (host, port) = address.split(':')
                Config(name, host, port.toInt())
            }
        }

        private fun othersAddrs(nodeName: String, config: List<Config>): List<MyAddr> {
            return config.filter { it.name != nodeName }.map { MyAddr(it.host, it.port) }
        }

        private val HELLO_PREFIX = "###hello.from"
        private val RESPONSE_PREFIX = "###response.from"
    }
}