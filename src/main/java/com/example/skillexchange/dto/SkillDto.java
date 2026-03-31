package com.example.skillexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for adding a new skill.
 * Client sends: { "userId": 1, "skillName": "Java", "description": "..." }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillDto {

    private Long userId;
    private String skillName;
    private String description;
}
