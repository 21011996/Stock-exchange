package messages

import java.io.InputStream
import java.nio.ByteBuffer

/**
 * Created by kirill on 04.04.17.
 */
data class Record(val name: String, val header: String, val message: String) {

    fun toByteArray(): ByteArray {
        val size = 16 + name.length + header.length + message.length
        var result = intToByteArray(size)
        result += intToByteArray(name.length)
        result += name.toByteArray()
        result += intToByteArray(header.length)
        result += header.toByteArray()
        result += intToByteArray(message.length)
        result += message.toByteArray()
        return result
    }

    companion object {
        fun fromInputStream(stream: InputStream): Record {
            var buf = ByteArray(1024 * 1024)
            stream.read(buf, 0, 4)
            var size = bytesToInt(buf)
            stream.read(buf, 0, size - 4)
            val params = (1..3).map {
                size = bytesToInt(buf.take(4).toByteArray())
                buf = buf.drop(4).toByteArray()
                val str = String(buf.take(size).toByteArray())
                buf = buf.drop(size).toByteArray()
                str
            }
            return Record(params[0], params[1], params[2])
        }

        private fun intToByteArray(i: Int) = ByteBuffer.allocate(4).putInt(i).array()
        private fun bytesToInt(byteArray: ByteArray) = ByteBuffer.wrap(byteArray).int
    }
}
