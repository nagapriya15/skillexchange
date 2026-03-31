package com.example.skillexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for sending a message.
 * Client sends: { "senderId": 1, "receiverId": 2, "message": "Hello!" }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {

    private Long senderId;
    private Long receiverId;
    private String message;
}
