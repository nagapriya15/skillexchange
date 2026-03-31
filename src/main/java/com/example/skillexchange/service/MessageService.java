package com.example.skillexchange.service;

import com.example.skillexchange.model.Message;
import com.example.skillexchange.model.User;
import com.example.skillexchange.repository.MessageRepository;
import com.example.skillexchange.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MessageService - Handles messaging between users.
 *
 * Methods:
 * - sendMessage() → Send a message from one user to another
 * - getChat()     → Get the full conversation between two users
 */
@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Send a message from one user to another.
     *
     * @param senderId   - ID of the user sending the message
     * @param receiverId - ID of the user receiving the message
     * @param content    - the message text
     * @return the saved Message object
     * @throws RuntimeException if sender or receiver not found
     */
    public Message sendMessage(Long senderId, Long receiverId, String content) {
        // Validate: cannot message yourself
        if (senderId.equals(receiverId)) {
            throw new RuntimeException("You cannot send a message to yourself!");
        }

        // Find sender
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found with ID: " + senderId));

        // Find receiver
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found with ID: " + receiverId));

        // Create the message
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setMessage(content);
        message.setTimestamp(LocalDateTime.now());

        // Save and return
        return messageRepository.save(message);
    }

    /**
     * Get the full chat conversation between two users.
     * Returns messages in both directions (A→B and B→A), sorted by time.
     *
     * @param userId1 - first user's ID
     * @param userId2 - second user's ID
     * @return list of messages between the two users, ordered by timestamp
     */
    public List<Message> getChat(Long userId1, Long userId2) {
        // Verify both users exist
        if (!userRepository.existsById(userId1)) {
            throw new RuntimeException("User not found with ID: " + userId1);
        }
        if (!userRepository.existsById(userId2)) {
            throw new RuntimeException("User not found with ID: " + userId2);
        }

        // Get messages in both directions (user1→user2 and user2→user1)
        return messageRepository
                .findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByTimestampAsc(
                        userId1, userId2,  // Messages from user1 to user2
                        userId2, userId1   // Messages from user2 to user1
                );
    }
}
