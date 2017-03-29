package messages;

import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

/**
 * @author ilya2
 *         created on 29.03.2017
 */
public class MessageTest {
    private Message testMessage;

    @Before
    public void setTestMessage() {
        testMessage = new Message("name", "header", "message");
    }

    @Test
    public void toAndFromByteBuffer() throws Exception {
        ByteBuffer buffer = testMessage.toByteBuffer();
        Message fromBB = Message.fromByteBuffer(buffer);
        assertEquals(testMessage, fromBB);
    }
}