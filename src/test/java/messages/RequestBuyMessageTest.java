package messages;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by disoni on 02.04.17.
 */
public class RequestBuyMessageTest {
    private Record testRecord;

    @Before
    public void setTestMessage() {
        testRecord = new Record("name", "header", "file1,5000");
    }

    @Test
    public void parseRecord() throws Exception {
        RequestBuyMessage answer = RequestBuyMessage.parseRecord(testRecord);
        assertEquals(answer.getRequestFile(),"file1");
        assertEquals(answer.getRequestPrice(),5000);
    }
}
