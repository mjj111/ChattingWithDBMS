package shardingTest.lab.mysql.domain;

import jakarta.persistence.*;
import shardingTest.lab.mysql.controller.model.MessageRequest;
import java.util.Date;

@Entity
@Table(name = "message")
public class MySQLMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "sender_id")
    private Long senderId;

    @Column(name = "receiver_id")
    private Long receiverId;

    @Column(name = "send_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sendTime;

    @Column(name = "is_read")
    private Boolean isRead = false;

    public MySQLMessageEntity() {}

    public static MySQLMessageEntity from(MessageRequest request) {
        MySQLMessageEntity entity = new MySQLMessageEntity();
        entity.type = MessageType.TEXT;
        entity.content = request.content();
        entity.roomId = request.roomId();
        entity.senderId = request.senderId();
        entity.receiverId = request.receiverId();
        entity.isRead = request.isRead();
        entity.sendTime = new Date();
        return entity;
    }

    public long getRoomId() {
        return roomId;
    }

    public String getContent() {
        return content;
    }

    public long getSenderId() {
        return senderId;
    }

    public long getReceiverId() {
        return receiverId;
    }

    public boolean getIsRead() {
        return isRead;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
