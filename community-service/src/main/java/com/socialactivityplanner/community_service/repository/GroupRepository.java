package com.socialactivityplanner.community_service.repository;

import com.socialactivityplanner.community_service.entity.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, UUID> {

    Page<Group> findByCreatedById(UUID userId, Pageable pageable);
}

