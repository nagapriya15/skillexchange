package com.example.skillexchange.repository;

import com.example.skillexchange.model.RequestStatus;
import com.example.skillexchange.model.SkillRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRequestRepository extends JpaRepository<SkillRequest, Long> {

    // Find all requests sent by a user
    List<SkillRequest> findBySenderId(Long senderId);

    // Find all requests received by a user
    List<SkillRequest> findByReceiverId(Long receiverId);

    // Find requests by status
    List<SkillRequest> findByStatus(RequestStatus status);

    // Find requests received by a user with a specific status
    List<SkillRequest> findByReceiverIdAndStatus(Long receiverId, RequestStatus status);
}
