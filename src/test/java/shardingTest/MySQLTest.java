package shardingTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import shardingTest.lab.LabApplication;
import shardingTest.lab.mysql.controller.MySQLMessageController;
import shardingTest.lab.mysql.controller.model.MessageRequest;
import java.util.List;


@SpringBootTest(classes = LabApplication.class)
public class MySQLTest {

    @Autowired
    private MySQLMessageController messageController;

    private long startTime;

    @BeforeEach
    public void setUpStartTime() {
        startTime = System.currentTimeMillis();
    }

    @AfterEach
    public void tearDownStartTime() {
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + "ms");
    }

    @Test
    public void testSaveMessage() {
        MessageRequest request = new MessageRequest("Hello", 1L, 101L, 102L, false);

        var result = messageController.saveMessage(request);

        Assertions.assertEquals(HttpStatusCode.valueOf(200), result.getStatusCode());
    }

    @Test
    public void markMessagesAsRead() {
        Long givenRoomId = 102L;
        Long recieverId = 101L;

        var result = messageController.markMessagesAsRead(givenRoomId, recieverId);

        Assertions.assertEquals(HttpStatusCode.valueOf(200), result.getStatusCode());
    }

    @Test
    public void findMessages() {
        Long givenRoomId = 102L;
        Long lastMessageId = 101L;

        var result = messageController.findMessages(givenRoomId, lastMessageId);

        Assertions.assertEquals(HttpStatusCode.valueOf(200), result.getStatusCode());
    }

    @Test
    public void aggregateMessageSummaries() {
        List<Long> roomIds = List.of(17999L, 18000L, 18001L, 18002L);
        Long memberId = 101L;

        var result = messageController.aggregateMessageSummaries(roomIds, memberId);
        System.out.println(result.getBody());
        Assertions.assertEquals(HttpStatusCode.valueOf(200), result.getStatusCode());
    }
}
