package network

import messages.Message
import java.net.Socket
import java.util.*

/**
 * Created by kirill on 03.04.17.
 */
class FixedAddressesNetworkLogicImpl private constructor(val nodeName: String, val myAddr: MyAddr,
                                                         val others: List<MyAddr>) : AbstractNetworkLogic() {
    override fun send(node: String?, message: Message?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sendToSocket(message: Message, socket: Socket) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
    }
}