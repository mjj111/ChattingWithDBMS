package shardingTest.lab.mongodb.domain.repository;

import shardingTest.lab.mongodb.domain.MongoMessageEntity;
import shardingTest.lab.mysql.MessageSummary;

import java.util.List;

public interface CustomMessageRepository {
    void markMessagesAsRead(Long chatRoomId, Long readBy);
    List<MongoMessageEntity> findMessages(Long chatRoomId, String lastMessageId);
    List<MessageSummary> aggregateMessageSummaries(List<Long> chatRoomIds, Long memberId);
}