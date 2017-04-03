package network

import logic.NetworkLogic
import messages.Message
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

/**
 * Created by kirill on 03.04.17.
 */
class FixedAddressesNetworkLogicImpl(val nodeName: String) {//: NetworkLogic {

    data class MyAddr(val host: String, val port: Int)

    val HELLO_PREFIX = "hello.from"
    val RESPONSE_PREFIX = "response.from"

    val addressBook = mutableMapOf<String, MyAddr>()
    val connected = ConcurrentHashMap<String, Socket>()

    val serverSocket = ServerSocket(8890)// + (nodeName.last() - '0'))

    init {
        val confIS = FixedAddressesNetworkLogicImpl::class.java.getResourceAsStream("/addresses.conf")
        val lines = Scanner(confIS).useDelimiter("\\A").next().split('\n')
        for (line in lines) {
            val (name, address) = line.split(' ')
            if (name != nodeName) {
                val (host, port) = address.split(':')
                addressBook[name] = MyAddr(host, port.toInt())
            }
        }
        startServerSocket()
        tryToConnect()
    }

    fun startServerSocket() {
        Thread {
            var socket: Socket? = null
            while (true) {
                try {
                    socket = serverSocket.accept()
                    println("$socket accepted")
                    Thread({
                        val input = BufferedReader(InputStreamReader(socket!!.getInputStream()))
                        val output = DataOutputStream(socket!!.getOutputStream())

                        var line: String?
                        println("start tcp read cycle for $socket")
                        while (true) {
                            try {
                                line = input.readLine()
                                if (line == null)
                                    continue
                                if (line.startsWith(HELLO_PREFIX)) {
                                    val name = line.split(' ')[1].dropLast(1)
                                    connected[name] = socket!!
                                }
                                println("get $line from: $socket")

                            } catch (e: Exception) {
                                println("tcp reader thread error: $e")
                                socket?.close()
                                break
                            }
                        }
                    }).start()
                } catch (e: IOException) {
                    println("I/O error on serverSocket: " + e)
                    break
                }
            }
        }.start()
    }

    fun tryToConnect() {
        for ((name, addr) in addressBook) {
            if (!connected.containsKey(name)) {
                try {
                    val socket = Socket(addr.host, addr.port)
                    val output = DataOutputStream(socket.getOutputStream())
                    output.writeBytes("$HELLO_PREFIX $nodeName\n")
                    output.flush()
                    connected[name] = socket
                    println("connected to $socket")
                } catch (e: ConnectException) {

                }
            }
        }
    }

    fun send(node: String, message: String) {
        if (connected.contains(node)) {
            val out = DataOutputStream(connected[node]?.getOutputStream())
            val toSend = if (!message.endsWith('\n')) message + '\n' else message
            out.writeBytes(toSend)
            out.flush()
        }
    }

    /*override fun addMessageHandler(handler: Consumer<in Message>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }*/
}

fun main(args: Array<String>) {
    val fl = FixedAddressesNetworkLogicImpl("node1")
    while (true) {
        val input = readLine()!!
        if (input == "end") {
            break
        } else if (input == "book") {
            println(fl.connected)
        } else {
            val (node, msg) = input.split(' ')
            fl.send(node, msg)
        }
    }
}