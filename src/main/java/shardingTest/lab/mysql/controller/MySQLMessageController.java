package shardingTest.lab.mysql.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shardingTest.lab.mysql.MessageSummary;
import shardingTest.lab.mysql.config.ThreadLocalDatabaseContextHolder;
import shardingTest.lab.mysql.controller.model.MessageRequest;
import shardingTest.lab.mysql.domain.MySQLMessageEntity;
import shardingTest.lab.mysql.service.MySQLMessageService;

import java.util.List;

@RestController
@RequestMapping("/mysql")
public class MySQLMessageController {

    private final MySQLMessageService mySQLMessageService;

    public MySQLMessageController(MySQLMessageService mySQLMessageService) {
        this.mySQLMessageService = mySQLMessageService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveMessage(@RequestBody MessageRequest request) {
        ThreadLocalDatabaseContextHolder.setRoomId(request.roomId());
        MySQLMessageEntity result = mySQLMessageService.saveMessage(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/mark")
    public ResponseEntity<?> markMessagesAsRead(@RequestParam Long roomId, @RequestParam Long readBy) {
        ThreadLocalDatabaseContextHolder.setRoomId(roomId);
        mySQLMessageService.markMessagesAsRead(roomId, readBy);
        return ResponseEntity.ok("Messages in room " + roomId + " marked as read by " + readBy);
    }

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<?> findMessages(@PathVariable Long roomId, @RequestParam(required = false) Long lastMessageId) {
        ThreadLocalDatabaseContextHolder.setRoomId(roomId);
        List<MySQLMessageEntity> messages = mySQLMessageService.findMessages(roomId, lastMessageId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/summaries")
    public ResponseEntity<?> aggregateMessageSummaries(@RequestParam List<Long> roomIds, @RequestParam Long memberId) {
        List<MessageSummary> summaries = mySQLMessageService.aggregateMessageSummaries(roomIds, memberId);
        return ResponseEntity.ok(summaries);
    }
}