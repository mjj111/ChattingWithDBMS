package shardingTest.lab.mysql.domain;

import jakarta.transaction.Transactional;
import org.aspectj.bridge.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MySQLMessageRepository extends JpaRepository<MySQLMessageEntity, Long> {

    @Modifying
    @Query("UPDATE MySQLMessageEntity m SET m.isRead = true WHERE m.roomId = :roomId AND m.receiverId = :readBy AND m.isRead = false")
    void markMessagesAsRead(@Param("roomId") Long roomId, @Param("readBy") Long readBy);

    @Query("SELECT m FROM MySQLMessageEntity m WHERE m.roomId = :roomId AND (m.id < :lastMessageId OR :lastMessageId IS NULL) ORDER BY m.id DESC")
    List<MySQLMessageEntity> findMessages(@Param("roomId") Long roomId, @Param("lastMessageId") Long lastMessageId);


    @Query(value = """
    SELECT 
        m.room_id AS roomId,
        MAX(m.send_time) AS lastMessageTime,
        (SELECT m2.content 
         FROM message m2 
         WHERE m2.room_id = m.room_id 
         ORDER BY m2.send_time DESC 
         LIMIT 1) AS lastMessageContent,
        SUM(CASE 
            WHEN m.receiver_id = :memberId AND m.is_read = false THEN 1 
            ELSE 0 
        END) AS numberOfUnreadMessages
    FROM 
        message m
    WHERE 
        m.room_id IN (:roomIds)
    GROUP BY 
        m.room_id
    ORDER BY 
        lastMessageTime DESC
""", nativeQuery = true)
    List<Object[]> aggregateMessageSummaries(
            @Param("roomIds") List<Long> roomIds,
            @Param("memberId") Long memberId
    );


}