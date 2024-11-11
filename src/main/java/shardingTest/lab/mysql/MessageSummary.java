package shardingTest.lab.mysql;

import java.util.Date;

public record MessageSummary(Long roomId, Date lastMessageTime, String lastMessageContent, Long numberOfUnreadMessages) {
}
