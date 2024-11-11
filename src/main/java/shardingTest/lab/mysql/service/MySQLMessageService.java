package shardingTest.lab.mysql.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import shardingTest.lab.mysql.MessageSummary;
import shardingTest.lab.mysql.config.ThreadLocalDatabaseContextHolder;
import shardingTest.lab.mysql.controller.model.MessageRequest;
import shardingTest.lab.mysql.domain.MySQLMessageEntity;
import shardingTest.lab.mysql.domain.MySQLMessageRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MySQLMessageService {

    private final MySQLMessageRepository mySQLMessageRepository;

    public MySQLMessageService(MySQLMessageRepository mySQLMessageRepository) {
        this.mySQLMessageRepository = mySQLMessageRepository;
    }

    @Transactional
    public MySQLMessageEntity saveMessage(MessageRequest request) {
        MySQLMessageEntity message = MySQLMessageEntity.from(request);
        return mySQLMessageRepository.save(message);
    }

    @Transactional
    public void markMessagesAsRead(Long roomId, Long readBy) {
        mySQLMessageRepository.markMessagesAsRead(roomId, readBy);
    }

    public List<MySQLMessageEntity> findMessages(Long roomId, Long lastMessageId) {
        return mySQLMessageRepository.findMessages(roomId, lastMessageId);
    }

    public List<MessageSummary> aggregateMessageSummaries(List<Long> roomIds, Long memberId) {
        List<MessageSummary> summaries = new ArrayList<>();

        for (Long roomId : roomIds) {
            ThreadLocalDatabaseContextHolder.setRoomId(roomId);

            List<Object[]> results = mySQLMessageRepository.aggregateMessageSummaries(List.of(roomId), memberId);

            List<MessageSummary> roomSummaries = results.stream()
                    .map(row -> new MessageSummary(
                            ((Number) row[0]).longValue(),
                            (Date) row[1],
                            (String) row[2],
                            ((Number) row[3]).longValue()
                    ))
                    .toList();

            summaries.addAll(roomSummaries);
        }

        return summaries;
    }
}
