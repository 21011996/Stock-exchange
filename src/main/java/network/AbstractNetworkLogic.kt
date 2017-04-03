package network

import logic.NetworkLogic
import messages.Message
import java.net.Socket
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

/**
 * Created by kirill on 03.04.17.
 */
abstract class AbstractNetworkLogic : NetworkLogic {

    protected val addressBook = ConcurrentHashMap<String, Socket>()
    protected val messageHandlers: MutableList<Consumer<in Message>>
            = Collections.synchronizedList(mutableListOf<Consumer<in Message>>())

    override fun sendAll(message: Message) {
        for (socket in addressBook.values) {
            sendToSocket(message, socket)
        }
    }

    protected abstract fun sendToSocket(message: Message, socket: Socket)

    override fun addMessageHandler(handler: Consumer<in Message>) {
        messageHandlers.add(handler)
    }

    override fun getNodes(): MutableSet<String> = Collections.unmodifiableSet(addressBook.keys)

}

class NoSuchNodeException(val node: String) : IllegalArgumentException("Unknown node: $node")