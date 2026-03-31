package com.example.skillexchange.repository;

import com.example.skillexchange.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    // Find all skills offered by a specific user
    List<Skill> findByUserId(Long userId);

    // Search skills by name (case-insensitive)
    List<Skill> findBySkillNameContainingIgnoreCase(String skillName);
}
