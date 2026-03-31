package com.example.skillexchange.controller;

import com.example.skillexchange.dto.SkillDto;
import com.example.skillexchange.model.Skill;
import com.example.skillexchange.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * SkillController - REST API for skill management.
 *
 * Base Path: /api/skills
 * All endpoints require JWT token in Authorization header.
 *
 * Endpoints:
 * POST /api/skills/add             → Add a new skill
 * GET  /api/skills/user/{userId}   → Get all skills of a user
 * GET  /api/skills/search?keyword= → Search skills by keyword
 */
@RestController
@RequestMapping("/api/skills")
public class SkillController {

    @Autowired
    private SkillService skillService;

    // ==================== ADD SKILL ====================

    /**
     * POST /api/skills/add
     * Request Body: { "userId": 1, "skillName": "Java", "description": "..." }
     * Header: Authorization: Bearer <token>
     */
    @PostMapping("/add")
    public ResponseEntity<?> addSkill(@RequestBody SkillDto request) {
        try {
            Skill skill = skillService.addSkill(
                    request.getUserId(),
                    request.getSkillName(),
                    request.getDescription()
            );
            return ResponseEntity.ok(Map.of(
                    "message", "Skill added successfully!",
                    "skillId", skill.getId(),
                    "skillName", skill.getSkillName()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== GET SKILLS BY USER ====================

    /**
     * GET /api/skills/user/{userId}
     * Header: Authorization: Bearer <token>
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getSkillsByUser(@PathVariable Long userId) {
        try {
            List<Skill> skills = skillService.getSkillsByUser(userId);
            return ResponseEntity.ok(skills);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== SEARCH SKILLS ====================

    /**
     * GET /api/skills/search?keyword=java
     * Header: Authorization: Bearer <token>
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchSkills(@RequestParam String keyword) {
        List<Skill> skills = skillService.searchSkills(keyword);
        return ResponseEntity.ok(skills);
    }
}
