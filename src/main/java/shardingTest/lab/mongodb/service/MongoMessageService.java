package shardingTest.lab.mongodb.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import shardingTest.lab.mongodb.domain.MongoMessageEntity;
import shardingTest.lab.mongodb.domain.repository.MongoMessageRepository;
import shardingTest.lab.mysql.MessageSummary;
import shardingTest.lab.mysql.controller.model.MessageRequest;

import java.util.List;

@Service
public class MongoMessageService {

    private final MongoMessageRepository messageRepository;

    public MongoMessageService(MongoMessageRepository mongoMessageRepository) {
        this.messageRepository = mongoMessageRepository;
    }

    @Transactional
    public MongoMessageEntity saveMessage(MessageRequest request) {
        MongoMessageEntity message = MongoMessageEntity.from(request);
        return messageRepository.save(message);
    }

    @Transactional
    public void markMessagesAsRead(Long roomId, Long readBy) {
        messageRepository.markMessagesAsRead(roomId, readBy);
    }

    public List<MongoMessageEntity> findMessages(Long chatRoomId, String lastMessageId) {
        return messageRepository.findMessages(chatRoomId,lastMessageId);
    }

    public List<MessageSummary> aggregateMessageSummaries(List<Long> roomIds, Long memberId) {
        return messageRepository.aggregateMessageSummaries(roomIds, memberId);
    }
}
