package messages;

import org.junit.Before;
import org.junit.Test;
import files.File;

import static org.junit.Assert.assertEquals;

/**
 * Created by disoni on 02.04.17.
 */
public class HelloMessageTest {
    private Record testRecord;

    @Before
    public void setTestMessage() {
        testRecord = new Record("name", "header", "1000;file1=21,file2=20");
    }

    @Test
    public void parseRecord() throws Exception {
        HelloMessage answer = HelloMessage.parseRecord(testRecord);
        assertEquals(answer.getBalance(),1000);
        assertEquals(answer.getFiles().get(0).getName(),"file1");
        assertEquals(answer.getFiles().get(0).getPrice(),21);
        assertEquals(answer.getFiles().get(1).getName(),"file2");
        assertEquals(answer.getFiles().get(1).getPrice(),20);
    }
}
