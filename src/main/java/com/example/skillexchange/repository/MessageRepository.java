package com.example.skillexchange.repository;

import com.example.skillexchange.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Find all messages between two users (conversation)
    List<Message> findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByTimestampAsc(
            Long senderId1, Long receiverId1,
            Long senderId2, Long receiverId2
    );

    // Find all messages sent by a user
    List<Message> findBySenderIdOrderByTimestampDesc(Long senderId);

    // Find all messages received by a user
    List<Message> findByReceiverIdOrderByTimestampDesc(Long receiverId);
}
