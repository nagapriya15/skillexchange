package com.example.skillexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for submitting feedback.
 * Client sends: { "userId": 1, "rating": 5, "comment": "Great teacher!" }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDto {

    private Long userId;
    private Integer rating;
    private String comment;
}
