package network

import java.util.*
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.MulticastSocket

/**
 * Created by kirill on 02.04.17.
 */

fun main(args: Array<String>) {
    InputDataThread("main-2", 5000).run()
    //InputDataThread("main-2", 5000).run()
}

class InputDataThread(val nodeName: String, val sleepTime: Long) : Thread("InputDataThread") {

    val receiveSocket = MulticastSocket(4446)
    val sendSocket = MulticastSocket(4445)
    val address: InetAddress = InetAddress.getByName("230.0.0.1") //TODO: change address

    init {
        receiveSocket.joinGroup(address)
    }

    override fun run() {
        var packet: DatagramPacket
        sleep(sleepTime)

        var buf = ByteArray(256)
        buf = "hello, my name is $nodeName".toByteArray()

        packet = DatagramPacket(buf, buf.size, address, 4446)
        sendSocket.send(packet)

        while (true) {
            //sleep(sleepTime)
            try {
                packet = DatagramPacket(buf, buf.size)
                receiveSocket.receive(packet)

                val received = String(packet.data, 0, packet.length)
                println("get `$received` from ${packet.address}")

                val address = packet.address
                val port = packet.port

                buf = "${Date()}: my name is $nodeName".toByteArray()

                packet = DatagramPacket(buf, buf.size, address, port)
                receiveSocket.send(packet)

            } catch (e: Exception) {
                receiveSocket.leaveGroup(address)
                receiveSocket.close()
            }
        }
    }
}

