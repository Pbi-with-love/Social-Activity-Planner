package com.socialactivityplanner.community_service.dto.response;

import com.socialactivityplanner.community_service.entity.GroupRole;
import java.time.Instant;
import java.util.UUID;

public record GroupMemberResponse(
    UUID groupId, UUID userId, String username, GroupRole role, Instant joinedAt) {}
