package com.socialactivityplanner.community_service.dto.request;

import com.socialactivityplanner.community_service.entity.GroupRole;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GroupMemberAddRequest(
    @NotNull UUID userId,

    GroupRole role) {
}
