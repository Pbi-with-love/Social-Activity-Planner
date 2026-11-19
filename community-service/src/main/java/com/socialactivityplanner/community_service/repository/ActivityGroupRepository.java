package com.socialactivityplanner.community_service.repository;

import com.socialactivityplanner.community_service.entity.ActivityGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActivityGroupRepository extends JpaRepository<ActivityGroup, UUID> {

  Page<ActivityGroup> findByCreatedById(UUID userId, Pageable pageable);

  boolean existsByCreatedById(UUID userId);
}
