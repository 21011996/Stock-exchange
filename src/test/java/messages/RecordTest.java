package messages;

import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

/**
 * @author ilya2
 *         created on 29.03.2017
 */
public class RecordTest {
    private Record testRecord;

    @Before
    public void setTestMessage() {
        testRecord = new Record("name", "header", "message");
    }

    @Test
    public void toAndFromByteBuffer() throws Exception {
        ByteBuffer buffer = testRecord.toByteBuffer();
        Record fromBB = Record.fromByteBuffer(buffer);
        assertEquals(testRecord, fromBB);
    }
}