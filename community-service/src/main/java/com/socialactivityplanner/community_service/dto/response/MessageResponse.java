package com.socialactivityplanner.community_service.dto.response;

import java.time.Instant;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        UUID groupId,
        UUID senderId,
        String senderUsername,
        String content,
        Instant sentAt,
        Instant updatedAt
) {}
