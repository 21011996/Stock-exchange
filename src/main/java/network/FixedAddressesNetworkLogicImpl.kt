package network

import logic.NetworkLogic
import messages.Message
import java.io.IOException
import java.net.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

/**
 * Created by kirill on 03.04.17.
 */
class FixedAddressesNetworkLogicImpl(val nodeName: String) : NetworkLogic {

    data class MyAddr(val host: String, val port: Int)

    val addressBook = mutableMapOf<String, MyAddr>()
    val connected = ConcurrentHashMap<String, Socket>()

    val serverSocket = ServerSocket(8890 + (nodeName.last() - '0'))

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
    }

    fun startServerSocket() {
        Thread {
            var socket: Socket? = null
            while (true) {
                try {
                    socket = serverSocket.accept()
                    println("$socket accepted")
                    Thread({

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
                    connected[name] = socket
                    println("connected to $socket")
                } catch (e: ConnectException) {

                }
            }
        }
    }

    override fun send(node: String?, message: Message?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addMessageHandler(handler: Consumer<in Message>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

fun main(args: Array<String>) {
    val fl = FixedAddressesNetworkLogicImpl("user1")
    println(("user1".last() - '0'))
}