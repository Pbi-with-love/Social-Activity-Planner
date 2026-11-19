package com.socialactivityplanner.community_service.repository;

import com.socialactivityplanner.community_service.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  Page<Message> findByConversationIdOrderByIdDesc(UUID conversationId, Pageable pageable);

  Optional<Message> findByIdAndConversationId(UUID messageId, UUID conversationId);
}
