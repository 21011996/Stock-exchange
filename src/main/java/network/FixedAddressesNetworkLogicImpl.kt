package network

import messages.HandShakeHelloMessage
import messages.HandShakeResponseMessage
import messages.Message
import messages.Record
import java.io.*
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
            val input = socket.getInputStream()
            println("start tcp read cycle for $socket")
            while (!Thread.interrupted()) {
                try {
                    val message = Message.parseRecord(Record.fromInputStream(input))
                    when (message) {
                        is HandShakeHelloMessage -> {
                            addressBook[message.name] = socket
                            sendToSocket(HandShakeResponseMessage(nodeName), socket)
                        }
                        is HandShakeResponseMessage -> {
                            if (!addressBook.containsKey(message.name)) {
                                addressBook[message.name] = socket
                            }
                        }
                        else -> {
                            messageHandlers.forEach { it.accept(message) }
                        }
                    }
                    println("get $message from: $socket")
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
                sendToSocket(HandShakeHelloMessage(nodeName), socket)
                println("connected to $socket")
                handleSocket(socket)
            } catch (ignored: ConnectException) {
            }
        }
    }

    override fun send(node: String, message: Message) {
        sendToSocket(message, addressBook[node] ?: throw NoSuchNodeException(node))
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