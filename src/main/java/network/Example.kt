package network

import java.net.DatagramPacket
import java.net.InetAddress
import java.net.MulticastSocket
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by kirill on 02.04.17.
 */

sealed class HandshakeMessage(val senderName: String)

class RequestName(name: String) : HandshakeMessage(name)
class ResponseToRequestName(name: String) : HandshakeMessage(name)

data class NodeInfo(val address: InetAddress, val port: Int)

fun main(args: Array<String>) {
    val addressBook = ConcurrentHashMap<String, NodeInfo>()
    InputDataThread("main-3", addressBook).run()
}

class InputDataThread(val nodeName: String, val addressBook: ConcurrentHashMap<String, NodeInfo>) : Thread("InputDataThread") {

    val HELLO_PREFIX = "hello.from"
    val RESPONSE_PREFIX = "response.from"

    val receiveSocket = MulticastSocket(4446)
    val sendSocket = MulticastSocket(4445)
    val address: InetAddress = InetAddress.getByName("239.0.0.1") //TODO: change address

    init {
        receiveSocket.joinGroup(address)
    }

    override fun run() {
        println("node $nodeName")
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
                        addressBook.put(name, NodeInfo(address, port))
                        println("book names: ${addressBook.keys.joinToString(", ")}")
                        buf = "$RESPONSE_PREFIX $nodeName".toByteArray()
                        packet = DatagramPacket(buf, buf.size, address, 4446)
                        sendSocket.send(packet)
                    }
                } else if (received.startsWith(RESPONSE_PREFIX)) {
                    val name = received.split(' ')[1]
                    addressBook.put(name, NodeInfo(address, port))
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

