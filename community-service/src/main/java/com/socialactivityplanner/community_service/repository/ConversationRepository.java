package com.socialactivityplanner.community_service.repository;

import com.socialactivityplanner.community_service.entity.Conversation;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {}
