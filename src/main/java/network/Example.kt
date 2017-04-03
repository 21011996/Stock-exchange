package network

import java.io.*
import java.net.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by kirill on 02.04.17.
 */

sealed class HandshakeMessage(val senderName: String)

class RequestName(name: String) : HandshakeMessage(name)
class ResponseToRequestName(name: String) : HandshakeMessage(name)

//data class Socket(val address: InetAddress, val port: Int)

fun main(args: Array<String>) {
    val a = ConcurrentHashMap<String, RequestName>()
    val rf = RequestName("foo")
    val rb = RequestName("bar")
    a["foo"] = rf
    a["boo"] = rb
    println(a)
    a.values.remove(rf)
    println(a)
    println(a.keys)
    //val addressBook = ConcurrentHashMap<String, Socket>()
    //IOThread("main-1", addressBook).run()
    //InputDataThread("main-3", addressBook).run()
}

class IOThread(val nodeName: String, val addressBook: ConcurrentHashMap<String, Socket>) : Thread("InputDataThread") {
    val HELLO_PREFIX = "hello.from"
    val RESPONSE_PREFIX = "response.from"

    val tcpServerSocket = ServerSocket(4450)
    val receiveSocket = MulticastSocket(4446)
    val sendSocket = MulticastSocket(4445)
    val address: InetAddress = InetAddress.getByName("239.0.0.1") //TODO: change address

    init {
        receiveSocket.joinGroup(address)
    }

    override fun run() {
        println("node $nodeName")

        Thread({
            var socket: Socket? = null
            while (true) {
                try {
                    socket = tcpServerSocket.accept()
                    println("$socket accepted")
                    Thread({
                        val input = BufferedReader(InputStreamReader(socket!!.getInputStream()))
                        val output = DataOutputStream(socket!!.getOutputStream())

                        var line: String?
                        while (true) {
                            println("start tcp read cycle for $socket")
                            try {

                                line = input.readLine()
                                println("line = $line")
                                if (line == null)
                                    continue
                                if (line.startsWith(HELLO_PREFIX)) {
                                    val name = line.split(' ')[1].dropLast(1)
                                    println("get hello from: $name")
                                    if (name == nodeName) {
                                        println("... hello from self")
                                        continue
                                    }
                                    addressBook[name] = socket!!
                                    println("book names: ${addressBook.keys.joinToString(", ")}")
                                    output.writeBytes("$RESPONSE_PREFIX $nodeName\n")
                                    output.flush()
                                } else if (line.startsWith(RESPONSE_PREFIX)) {
                                    val name = line.split(' ')[1].dropLast(1)
                                    println("get response from: $name")
                                    addressBook[name] = socket!!
                                    println("book names: ${addressBook.keys.joinToString(", ")}")
                                }
                            } catch (e: Exception) {
                                println("tcp reader thread error: $e")
                            }
                        }
                    }).start()
                } catch (e: IOException) {
                    println("I/O error: " + e)
                    break
                }
            }
        }).start()

        var packet: DatagramPacket
        var buf = "!!".toByteArray()
        packet = DatagramPacket(buf, buf.size, address, 4446)
        //sendSocket.send(packet)
        receiveSocket.send(packet)

        while (true) {
            packet = DatagramPacket(buf, buf.size)
            receiveSocket.receive(packet)

            val received = String(packet.data, 0, packet.length)
            println("get udp packet from ${packet.address}")

            val clientSocket = Socket(packet.address, 4450)
            val outToServer = DataOutputStream(clientSocket.getOutputStream())
            outToServer.writeBytes("$HELLO_PREFIX $nodeName\n")
            outToServer.flush()

        }
    }

}

class InputDataThread(val nodeName: String, val addressBook: ConcurrentHashMap<String, Socket>) : Thread("InputDataThread") {

    val HELLO_PREFIX = "hello.from"
    val RESPONSE_PREFIX = "response.from"

    val tcpServerSocket = ServerSocket(4450)


    val receiveSocket = MulticastSocket(4446)
    val sendSocket = MulticastSocket(4445)
    val address: InetAddress = InetAddress.getByName("239.0.0.1") //TODO: change address

    init {
        receiveSocket.joinGroup(address)
    }

    override fun run() {
        println("node $nodeName")
        Thread("tcp server thread").run {
            var socket: java.net.Socket? = null
            while (true) {
                try {
                    socket = tcpServerSocket.accept()
                } catch (e: IOException) {
                    println("I/O error: " + e)
                    break
                }

                //EchoThread(socket!!).start()
            }
        }
        var packet: DatagramPacket

        var buf = "$HELLO_PREFIX $nodeName".toByteArray()

        packet = DatagramPacket(buf, buf.size, address, 4446)
        sendSocket.send(packet)

        while (true) {
            try {
                packet = DatagramPacket(buf, buf.size)
                receiveSocket.receive(packet)

                val received = String(packet.data, 0, packet.length)
                println("get `$received` from ${packet.address}")

                val address = packet.address
                val port = packet.port

                if (received.startsWith(HELLO_PREFIX)) {
                    val name = received.split(' ')[1]
                    if (name == nodeName) {
                        println("get hello message from self")
                    } else {
                        addressBook.put(name, Socket(address, port))
                        println("book names: ${addressBook.keys.joinToString(", ")}")
                        buf = "$RESPONSE_PREFIX $nodeName".toByteArray()
                        packet = DatagramPacket(buf, buf.size, address, 4446)
                        sendSocket.send(packet)
                    }
                } else if (received.startsWith(RESPONSE_PREFIX)) {
                    val name = received.split(' ')[1]
                    addressBook.put(name, Socket(address, port))
                    println("book names: ${addressBook.keys.joinToString(", ")}")
                }

            } catch (e: Exception) {
                receiveSocket.leaveGroup(address)
                receiveSocket.close()
                sendSocket.close()
            }
        }
    }
}

