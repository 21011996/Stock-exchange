package messages;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by disoni on 02.04.17.
 */
public class TransferFileMessageTest {


    private Record testRecord;

    @Before
    public void setTestMessage() {
        testRecord = new Record("name", "header", "file,exception");
    }

    @Test
    public void parseRecord() throws Exception {
        RejectBuyMessage answer = RejectBuyMessage.parseRecord(testRecord);
        assertEquals(answer.getFileName(),"file");
        assertEquals(answer.getReason(),"exception");
    }
}
