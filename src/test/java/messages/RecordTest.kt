package messages

import org.junit.Before
import org.junit.Test

import org.junit.Assert.assertEquals

/**
 * @author ilya2
 * *         created on 29.03.2017
 */
class RecordTest {
    private var testRecord: Record? = null

    @Before
    fun setTestMessage() {
        testRecord = Record("name", "header", "message")
    }

    @Test
    fun toAndFromByteBuffer() {
        val buffer = testRecord!!.toByteArray()
        val fromBB = Record.fromInputStream(buffer.inputStream())
        assertEquals(testRecord, fromBB)
    }
}