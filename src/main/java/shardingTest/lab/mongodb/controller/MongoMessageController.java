package shardingTest.lab.mongodb.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shardingTest.lab.mongodb.domain.MongoMessageEntity;
import shardingTest.lab.mongodb.service.MongoMessageService;
import shardingTest.lab.mysql.MessageSummary;
import shardingTest.lab.mysql.controller.model.MessageRequest;

import java.util.List;

@RestController
@RequestMapping("/mongo")
public class MongoMessageController {

    private final MongoMessageService messageService;

    public MongoMessageController(MongoMessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveMessage(@RequestBody MessageRequest request) {
        var result = messageService.saveMessage(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/mark")
    public ResponseEntity<?> markMessagesAsRead(@RequestParam Long roomId, @RequestParam Long readBy) {
        messageService.markMessagesAsRead(roomId, readBy);
        return ResponseEntity.ok("Messages in room " + roomId + " marked as read by " + readBy);
    }

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<?> findMessages(@PathVariable Long roomId, @RequestParam(required = false) String lastMessageId) {
        List<MongoMessageEntity> messages = messageService.findMessages(roomId, lastMessageId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/summaries")
    public ResponseEntity<?> aggregateMessageSummaries(@RequestParam List<Long> roomIds, @RequestParam Long memberId) {
        List<MessageSummary> summaries = messageService.aggregateMessageSummaries(roomIds, memberId);
        return ResponseEntity.ok(summaries);
    }
}