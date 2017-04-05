package network

import logic.Node
import java.io.IOException
import java.net.*

/**
 * Created by kirill on 05.04.17.
 */
class NetworkLogicImpl
private constructor(node: Node, nodeName: String, myAddr: MyAddr,
                    others: List<MyAddr> = listOf()) : FixedAddressesNetworkLogicImpl(node, nodeName, myAddr, others) {

    val multicastSocket = MulticastSocket(myAddr.multicastPort)
    val address: InetAddress = InetAddress.getByName(myAddr.host)

    override fun start() {
        serverSocket = ServerSocket(myAddr.port)
        println("$nodeName started...")
        multicastSocket.joinGroup(address)
        startServerSocket()
        startMulticastListener()
    }

    /*init {
        println("$nodeName started...")
        multicastSocket.joinGroup(address)
        startServerSocket()
        startMulticastListener()
    }*/

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

            while (!Thread.interrupted()) {
                try {
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
                } catch (e: IOException) {
                    println("I/O error on multicastSocket: " + e)
                    multicastSocket.close()
                    break
                }
            }
        }).start()
    }

    private fun tcpSocketPortByHost(host: String) =
            others.filter { it.host == host }.first().port

    companion object {
        fun buildFromConfig(nodeName: String, node: Node): NetworkLogicImpl {
            val cfg = readConfig()
            val logic = NetworkLogicImpl(node, nodeName, thisNodeAddr(nodeName, cfg), othersAddrs(nodeName, cfg))
            logic.start()
            return logic
        }

        fun buildForNode(node: Node, addr: MyAddr): NetworkLogicImpl {
            return NetworkLogicImpl(node, node.name, addr, listOf())
        }
    }

}