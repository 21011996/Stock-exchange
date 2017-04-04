package messages

/**
 * Created by kirill on 04.04.17.
 */

sealed class HandShakeMessage(name: String) : Message(name)

class HandShakeHelloMessage(name: String) : HandShakeMessage(name) {
    override fun toRecord() = Record(name, "${MessageType.HANDSHAKE_HELLO}", "hello from $name")
}

class HandShakeResponseMessage(name: String) : HandShakeMessage(name) {
    override fun toRecord() = Record(name, "${MessageType.HANDSHAKE_RESPONSE}", "response from $name")
}
