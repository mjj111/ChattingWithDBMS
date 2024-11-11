package shardingTest.lab.mysql.controller.model;

public record MessageRequest(String content, Long roomId, Long senderId, Long receiverId, Boolean isRead) {
}