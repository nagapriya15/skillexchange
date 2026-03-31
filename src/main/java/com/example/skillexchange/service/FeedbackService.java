package com.example.skillexchange.service;

import com.example.skillexchange.model.Feedback;
import com.example.skillexchange.model.User;
import com.example.skillexchange.repository.FeedbackRepository;
import com.example.skillexchange.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * FeedbackService - Handles feedback/rating logic.
 *
 * Methods:
 * - addFeedback()        → Add a rating and comment for a user
 * - getFeedbackForUser() → Get all feedback received by a user
 */
@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Add feedback for a user.
     *
     * @param userId  - ID of the user being rated
     * @param rating  - rating value (1 to 5)
     * @param comment - optional comment/review text
     * @return the saved Feedback object
     * @throws RuntimeException if user not found or rating is invalid
     */
    public Feedback addFeedback(Long userId, Integer rating, String comment) {
        // Validate rating range
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        // Find the user being rated
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Create feedback
        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setRating(rating);
        feedback.setComment(comment);

        // Save and return
        return feedbackRepository.save(feedback);
    }

    /**
     * Get all feedback received by a specific user.
     *
     * @param userId - the user's ID
     * @return list of feedback for that user
     */
    public List<Feedback> getFeedbackForUser(Long userId) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        return feedbackRepository.findByUserId(userId);
    }
}
