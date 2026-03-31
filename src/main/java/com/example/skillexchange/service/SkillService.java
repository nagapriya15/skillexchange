package com.example.skillexchange.service;

import com.example.skillexchange.model.Skill;
import com.example.skillexchange.model.User;
import com.example.skillexchange.repository.SkillRepository;
import com.example.skillexchange.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * SkillService - Handles all skill-related business logic.
 *
 * Methods:
 * - addSkill()        → Add a new skill for a user
 * - getSkillsByUser() → Get all skills of a specific user
 * - searchSkills()    → Search skills by keyword
 */
@Service
public class SkillService {

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Add a new skill for a user.
     *
     * @param userId      - the ID of the user who has this skill
     * @param skillName   - name of the skill (e.g., "Java Programming")
     * @param description - description of the skill
     * @return the saved Skill object
     * @throws RuntimeException if user not found
     */
    public Skill addSkill(Long userId, String skillName, String description) {
        // Find the user first
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Create a new Skill and link it to the user
        Skill skill = new Skill();
        skill.setUser(user);
        skill.setSkillName(skillName);
        skill.setDescription(description);

        // Save and return
        return skillRepository.save(skill);
    }

    /**
     * Get all skills offered by a specific user.
     *
     * @param userId - the user's ID
     * @return list of skills belonging to that user
     */
    public List<Skill> getSkillsByUser(Long userId) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        return skillRepository.findByUserId(userId);
    }

    /**
     * Search skills by keyword (case-insensitive).
     * Useful for finding users who have a specific skill.
     *
     * @param keyword - the search term (e.g., "python")
     * @return list of matching skills
     */
    public List<Skill> searchSkills(String keyword) {
        return skillRepository.findBySkillNameContainingIgnoreCase(keyword);
    }
}
