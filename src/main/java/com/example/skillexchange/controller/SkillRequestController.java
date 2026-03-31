package com.example.skillexchange.controller;

import com.example.skillexchange.dto.SkillRequestDto;
import com.example.skillexchange.model.SkillRequest;
import com.example.skillexchange.service.SkillRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * SkillRequestController - REST API for skill exchange requests.
 *
 * Base Path: /api/requests
 * All endpoints require JWT token in Authorization header.
 *
 * Endpoints:
 * POST /api/requests/send              → Send a skill exchange request
 * PUT  /api/requests/accept/{id}       → Accept a pending request
 * PUT  /api/requests/reject/{id}       → Reject a pending request
 * GET  /api/requests/user/{userId}     → Get all requests for a user
 */
@RestController
@RequestMapping("/api/requests")
public class SkillRequestController {

    @Autowired
    private SkillRequestService skillRequestService;

    // ==================== SEND REQUEST ====================

    /**
     * POST /api/requests/send
     * Request Body: { "senderId": 1, "receiverId": 2, "skillId": 3 }
     * Header: Authorization: Bearer <token>
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendRequest(@RequestBody SkillRequestDto request) {
        try {
            SkillRequest skillRequest = skillRequestService.sendRequest(
                    request.getSenderId(),
                    request.getReceiverId(),
                    request.getSkillId()
            );
            return ResponseEntity.ok(Map.of(
                    "message", "Skill request sent successfully!",
                    "requestId", skillRequest.getId(),
                    "status", skillRequest.getStatus().toString()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== ACCEPT REQUEST ====================

    /**
     * PUT /api/requests/accept/{requestId}
     * Header: Authorization: Bearer <token>
     */
    @PutMapping("/accept/{requestId}")
    public ResponseEntity<?> acceptRequest(@PathVariable Long requestId) {
        try {
            SkillRequest skillRequest = skillRequestService.acceptRequest(requestId);
            return ResponseEntity.ok(Map.of(
                    "message", "Request accepted!",
                    "requestId", skillRequest.getId(),
                    "status", skillRequest.getStatus().toString()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== REJECT REQUEST ====================

    /**
     * PUT /api/requests/reject/{requestId}
     * Header: Authorization: Bearer <token>
     */
    @PutMapping("/reject/{requestId}")
    public ResponseEntity<?> rejectRequest(@PathVariable Long requestId) {
        try {
            SkillRequest skillRequest = skillRequestService.rejectRequest(requestId);
            return ResponseEntity.ok(Map.of(
                    "message", "Request rejected.",
                    "requestId", skillRequest.getId(),
                    "status", skillRequest.getStatus().toString()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== GET REQUESTS FOR USER ====================

    /**
     * GET /api/requests/user/{userId}
     * Header: Authorization: Bearer <token>
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getRequestsForUser(@PathVariable Long userId) {
        try {
            List<SkillRequest> requests = skillRequestService.getRequestsForUser(userId);
            return ResponseEntity.ok(requests);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
