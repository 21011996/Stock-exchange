package network

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by kirill on 03.04.17.
 */

class FixedAddressesNetworkLogic2(val nodeName: String, val myAddr: MyAddr, val others: List<MyAddr>) {
    val connected = ConcurrentHashMap<String, Socket>()
    val HELLO_PREFIX = "hello.from"
    val RESPONSE_PREFIX = "response.from"

    val serverSocket = ServerSocket(myAddr.port)

    init {
        println("$nodeName started...")
        startServerSocket()
        trySendHello()
    }

    fun startServerSocket() {
        Thread({
            while (true) {
                try {
                    val socket = serverSocket.accept()
                    println("$socket accepted")
                    Thread({
                        val input = BufferedReader(InputStreamReader(socket!!.getInputStream()))
                        val output = DataOutputStream(socket.getOutputStream())

                        var line: String?
                        println("start tcp read cycle for $socket")
                        while (true) {
                            try {
                                line = input.readLine() ?: continue
                                if (line.startsWith(HELLO_PREFIX)) {
                                    val name = line.split(' ')[1]
                                    connected[name] = socket
                                    output.writeBytes("$RESPONSE_PREFIX $nodeName\n")
                                    output.flush()
                                } else if (line.startsWith(RESPONSE_PREFIX)) {
                                    val name = line.split(' ')[1]
                                    if (!connected.containsKey(name)) {
                                        connected[name] = socket
                                    }
                                }
                                println("get $line from: $socket")

                            } catch (e: Exception) {
                                println("tcp reader thread error: $e")
                                socket.close()
                                break
                            }
                        }
                    }).start()
                } catch (e: IOException) {
                    println("I/O error on serverSocket: " + e)
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
            } catch (e: ConnectException) {

            }
        }
    }

    fun send(node: String, msg: String) {
        if (connected.containsKey(node)) {
            val output = DataOutputStream(connected[node]?.getOutputStream())
            output.writeBytes("$msg\n")
            output.flush()
            println("message sent")
        }
    }
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

fun readConfig(): List<Config> {
    val confIS = FixedAddressesNetworkLogic2::class.java.getResourceAsStream("/addresses.conf")
    val lines = Scanner(confIS).useDelimiter("\\A").next().split('\n')
    return lines.map { line ->
        val (name, address) = line.split(' ')
        val (host, port) = address.split(':')
        Config(name, host, port.toInt())
    }
}

fun othersAddrs(nodeName: String, config: List<Config>): List<MyAddr> {
    return config.filter { it.name != nodeName }.map { MyAddr(it.host, it.port) }
}

fun main(args: Array<String>) {
    val name = args[0]
    val cfg = readConfig()
    val logic = FixedAddressesNetworkLogic2(name, MyAddr.fromConfig(name, cfg), othersAddrs(name, cfg))
    while (true) {
        val input = readLine()!!
        if (input == "end") {
            break
        } else if (input == "book") {
            println(logic.connected)
        } else {
            val (node, msg) = input.split(' ')
            logic.send(node, msg)
        }
    }
}