package shardingTest.lab.mongodb.domain;

import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Sharded;
import shardingTest.lab.mysql.controller.model.MessageRequest;
import shardingTest.lab.mysql.domain.MessageType;

import java.util.Date;

@Document(collection = "message")
@Sharded(shardKey = {"roomId"})
public class MongoMessageEntity {
    @Id
    private String id;
    private MessageType type;
    private String content;
    private Long roomId;
    private Long senderId;
    private Long receiverId;
    private Date sendTime;
    private Boolean isRead;

    protected MongoMessageEntity() {
    }

    public static MongoMessageEntity from(MessageRequest request){
        MongoMessageEntity message = new MongoMessageEntity();
        message.type = MessageType.TEXT;
        message.content = request.content();
        message.roomId = request.roomId();
        message.senderId = request.senderId();
        message.receiverId = request.receiverId();
        message.sendTime = new Date();
        message.isRead = request.isRead();
        return message;
    }

    public String getId() {
        return id;
    }

    public MessageType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public Long getRoomId() {
        return roomId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", type=" + type +
                ", content='" + content + '\'' +
                ", roomId=" + roomId +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", sendTime=" + sendTime +
                ", isRead=" + isRead +
                '}';
    }
}
