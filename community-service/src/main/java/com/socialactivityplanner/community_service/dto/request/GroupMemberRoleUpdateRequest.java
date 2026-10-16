package com.socialactivityplanner.community_service.dto.request;

import com.socialactivityplanner.community_service.entity.GroupRole;
import jakarta.validation.constraints.NotNull;

public record GroupMemberRoleUpdateRequest(
        @NotNull
        GroupRole role
) {}
