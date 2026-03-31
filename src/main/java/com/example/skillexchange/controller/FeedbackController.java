package com.example.skillexchange.controller;

import com.example.skillexchange.dto.FeedbackDto;
import com.example.skillexchange.model.Feedback;
import com.example.skillexchange.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * FeedbackController - REST API for user feedback and ratings.
 *
 * Base Path: /api/feedback
 * All endpoints require JWT token in Authorization header.
 *
 * Endpoints:
 * POST /api/feedback/add            → Add feedback for a user
 * GET  /api/feedback/user/{userId}   → Get all feedback for a user
 */
@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    // ==================== ADD FEEDBACK ====================

    /**
     * POST /api/feedback/add
     * Request Body: { "userId": 1, "rating": 5, "comment": "Great teacher!" }
     * Header: Authorization: Bearer <token>
     */
    @PostMapping("/add")
    public ResponseEntity<?> addFeedback(@RequestBody FeedbackDto request) {
        try {
            Feedback feedback = feedbackService.addFeedback(
                    request.getUserId(),
                    request.getRating(),
                    request.getComment()
            );
            return ResponseEntity.ok(Map.of(
                    "message", "Feedback submitted successfully!",
                    "feedbackId", feedback.getId(),
                    "rating", feedback.getRating()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== GET FEEDBACK FOR USER ====================

    /**
     * GET /api/feedback/user/{userId}
     * Header: Authorization: Bearer <token>
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getFeedbackForUser(@PathVariable Long userId) {
        try {
            List<Feedback> feedbacks = feedbackService.getFeedbackForUser(userId);
            return ResponseEntity.ok(feedbacks);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
