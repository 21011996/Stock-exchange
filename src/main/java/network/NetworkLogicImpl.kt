package network

import logic.Node
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.MulticastSocket
import java.net.Socket

/**
 * Created by kirill on 05.04.17.
 */
class NetworkLogicImpl(node: Node, myAddr: MyAddr) : FixedAddressesNetworkLogicImpl(node, myAddr = myAddr) {

    val multicastSocket = MulticastSocket(myAddr.multicastPort)
    val address: InetAddress = InetAddress.getByName(myAddr.host)

    init {
        println("$nodeName started...")
        multicastSocket.joinGroup(address)
        startServerSocket()
    }

    private fun startMulticastListener() {
        Thread({
            val multicastMsg = ByteArray(0)
            if (others.isNotEmpty()) { //FOR DEBUG ONLY
                others.forEach {
                    val packet = DatagramPacket(multicastMsg, multicastMsg.size, address, it.multicastPort)
                    multicastSocket.send(packet)
                }
            } else { //normal multicast, all nodes listen the same port
                val packet = DatagramPacket(multicastMsg, multicastMsg.size, address, myAddr.port)
                multicastSocket.send(packet)
            }

            while (true) {
                val packet = DatagramPacket(multicastMsg, multicastMsg.size)
                multicastSocket.receive(packet)
                println("get udp packet from ${packet.address}")
                val tcpPort = if (others.isNotEmpty()) //FOR DEBUG ONLY
                    tcpSocketPortByHost(packet.address.hostAddress)
                else
                    myAddr.multicastPort
                val socket = Socket(packet.address.hostAddress, 333)

                val clientSocket = Socket(packet.address, tcpPort)
                sayHello(socket)
                handleSocket(socket)
            }
        }).start()
    }

    private fun tcpSocketPortByHost(host: String) =
            others.filter { it.host == host }.first().port

}