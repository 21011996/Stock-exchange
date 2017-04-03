package network

import logic.NetworkLogic
import messages.Message
import java.util.function.Consumer

/**
 * Created by kirill on 03.04.17.
 */
class FixedAddressesNetworkLogicImpl : NetworkLogic  {
    override fun send(node: String?, message: Message?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addMessageHandler(handler: Consumer<in Message>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}