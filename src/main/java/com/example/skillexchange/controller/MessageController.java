package com.example.skillexchange.controller;

import com.example.skillexchange.dto.MessageDto;
import com.example.skillexchange.model.Message;
import com.example.skillexchange.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * MessageController - REST API for messaging between users.
 *
 * Base Path: /api/messages
 * All endpoints require JWT token in Authorization header.
 *
 * Endpoints:
 * POST /api/messages/send                          → Send a message
 * GET  /api/messages/chat?senderId=1&receiverId=2  → Get chat history
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    // ==================== SEND MESSAGE ====================

    /**
     * POST /api/messages/send
     * Request Body: { "senderId": 1, "receiverId": 2, "message": "Hello!" }
     * Header: Authorization: Bearer <token>
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody MessageDto request) {
        try {
            Message message = messageService.sendMessage(
                    request.getSenderId(),
                    request.getReceiverId(),
                    request.getMessage()
            );
            return ResponseEntity.ok(Map.of(
                    "message", "Message sent successfully!",
                    "messageId", message.getId(),
                    "timestamp", message.getTimestamp().toString()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== GET CHAT HISTORY ====================

    /**
     * GET /api/messages/chat?senderId=1&receiverId=2
     * Returns all messages between two users, sorted by time.
     * Header: Authorization: Bearer <token>
     */
    @GetMapping("/chat")
    public ResponseEntity<?> getChat(@RequestParam Long senderId, @RequestParam Long receiverId) {
        try {
            List<Message> messages = messageService.getChat(senderId, receiverId);
            return ResponseEntity.ok(messages);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
