package com.example.skillexchange.repository;

import com.example.skillexchange.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // Find all feedbacks for a specific user
    List<Feedback> findByUserId(Long userId);
}
