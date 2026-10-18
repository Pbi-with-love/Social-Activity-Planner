package com.socialactivityplanner.community_service.service;

import com.socialactivityplanner.community_service.dto.response.GroupResponse;
import com.socialactivityplanner.community_service.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GroupService extends JpaRepository<Group, UUID> {
    GroupResponse findByName(String name);
}
