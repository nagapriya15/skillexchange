package com.example.skillexchange.service;

import com.example.skillexchange.model.RequestStatus;
import com.example.skillexchange.model.Skill;
import com.example.skillexchange.model.SkillRequest;
import com.example.skillexchange.model.User;
import com.example.skillexchange.repository.SkillRepository;
import com.example.skillexchange.repository.SkillRequestRepository;
import com.example.skillexchange.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * SkillRequestService - Handles skill exchange request logic.
 *
 * Flow:
 * 1. User A sees User B has a skill they want
 * 2. User A sends a skill request to User B → status = PENDING
 * 3. User B can ACCEPT or REJECT the request
 * 4. If accepted, they can communicate via messages
 *
 * Methods:
 * - sendRequest()        → Create a new skill exchange request
 * - acceptRequest()      → Accept a pending request
 * - rejectRequest()      → Reject a pending request
 * - getRequestsForUser() → Get all requests received by a user
 */
@Service
public class SkillRequestService {

    @Autowired
    private SkillRequestRepository skillRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SkillRepository skillRepository;

    /**
     * Send a new skill exchange request.
     *
     * @param senderId   - ID of the user sending the request
     * @param receiverId - ID of the user receiving the request
     * @param skillId    - ID of the skill being requested
     * @return the saved SkillRequest object
     * @throws RuntimeException if sender, receiver, or skill not found
     */
    public SkillRequest sendRequest(Long senderId, Long receiverId, Long skillId) {
        // Validate: sender and receiver must be different users
        if (senderId.equals(receiverId)) {
            throw new RuntimeException("You cannot send a request to yourself!");
        }

        // Find sender
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found with ID: " + senderId));

        // Find receiver
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found with ID: " + receiverId));

        // Find skill
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found with ID: " + skillId));

        // Create the skill request with PENDING status
        SkillRequest request = new SkillRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setSkill(skill);
        request.setStatus(RequestStatus.PENDING);

        // Save and return
        return skillRequestRepository.save(request);
    }

    /**
     * Accept a pending skill request.
     *
     * @param requestId - ID of the request to accept
     * @return the updated SkillRequest object
     * @throws RuntimeException if request not found or not pending
     */
    public SkillRequest acceptRequest(Long requestId) {
        SkillRequest request = skillRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Skill request not found with ID: " + requestId));

        // Only pending requests can be accepted
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Request is already " + request.getStatus());
        }

        request.setStatus(RequestStatus.ACCEPTED);
        return skillRequestRepository.save(request);
    }

    /**
     * Reject a pending skill request.
     *
     * @param requestId - ID of the request to reject
     * @return the updated SkillRequest object
     * @throws RuntimeException if request not found or not pending
     */
    public SkillRequest rejectRequest(Long requestId) {
        SkillRequest request = skillRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Skill request not found with ID: " + requestId));

        // Only pending requests can be rejected
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Request is already " + request.getStatus());
        }

        request.setStatus(RequestStatus.REJECTED);
        return skillRequestRepository.save(request);
    }

    /**
     * Get all skill requests received by a user.
     *
     * @param userId - the receiver's user ID
     * @return list of skill requests for that user
     */
    public List<SkillRequest> getRequestsForUser(Long userId) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        return skillRequestRepository.findByReceiverId(userId);
    }
}
