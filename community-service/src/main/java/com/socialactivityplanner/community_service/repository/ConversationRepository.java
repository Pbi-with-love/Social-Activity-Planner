package com.socialactivityplanner.community_service.repository;

import com.socialactivityplanner.community_service.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
}
