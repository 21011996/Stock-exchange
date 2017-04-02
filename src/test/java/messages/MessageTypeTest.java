package messages;

import org.junit.Test;


import static org.junit.Assert.assertEquals;

/**
 * Created by disoni on 02.04.17.
 */
public class MessageTypeTest {
    private String test1 = "hello";
    private String test2 = "have_money";
    private String test3 = "broke";
    private String test4 = "error";
    private String test5 = "request_buy";
    private String test6 = "reject_buy";
    private String test7 = "notify_buy";
    private String test8 = "transfer_file";
    private String test9 = "accept_buy";

    @Test
    public void parseType() throws Exception {
        MessageType type1 = MessageType.parse(test1);
        MessageType type2 = MessageType.parse(test2);
        MessageType type3 = MessageType.parse(test3);
        MessageType type4 = MessageType.parse(test4);
        MessageType type5 = MessageType.parse(test5);
        MessageType type6 = MessageType.parse(test6);
        MessageType type7 = MessageType.parse(test7);
        MessageType type8 = MessageType.parse(test8);
        MessageType type9 = MessageType.parse(test9);
        assertEquals(type1,MessageType.HELLO);
        assertEquals(type2,MessageType.HAVE_MONEY);
        assertEquals(type3,MessageType.BROKE);
        assertEquals(type4,MessageType.ERROR);
        assertEquals(type5,MessageType.REQUEST_BUY);
        assertEquals(type6,MessageType.REJECT_BUY);
        assertEquals(type7,MessageType.NOTIFY_BUY);
        assertEquals(type8,MessageType.TRANSFER_FILE);
        assertEquals(type9,MessageType.ACCEPT_BUY);
    }
}
