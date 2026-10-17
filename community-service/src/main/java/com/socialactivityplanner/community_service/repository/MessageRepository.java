package com.socialactivityplanner.community_service.repository;

import com.socialactivityplanner.community_service.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    Page<Message> findByGroupIdOrderBySentAtDesc(UUID groupId, Pageable pageable);

    Optional<Message> findByIdAndGroupId(UUID messageId, UUID groupId);
}

