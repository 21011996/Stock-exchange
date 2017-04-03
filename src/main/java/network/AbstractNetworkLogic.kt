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

    private val addressBook = ConcurrentHashMap<String, Socket>()
    private val messageHandlers = Collections.synchronizedList(mutableListOf<Consumer<in Message>>())

    override fun sendAll(message: Message) {
        for (socket in addressBook.values) {
            sendToSocket(message, socket)
        }
    }

    protected abstract fun sendToSocket(message: Message, socket: Socket)

    override fun addMessageHandler(handler: Consumer<in Message>) {
        messageHandlers.add(handler)
    }
}

class NoSuchNodeException(val node: String) : IllegalArgumentException("Unknown node: $node")