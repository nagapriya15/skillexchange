package com.example.skillexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for sending a skill exchange request.
 * Client sends: { "senderId": 1, "receiverId": 2, "skillId": 3 }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillRequestDto {

    private Long senderId;
    private Long receiverId;
    private Long skillId;
}
