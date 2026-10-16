package com.socialactivityplanner.community_service.mapper;

import com.socialactivityplanner.community_service.dto.response.GroupResponse;
import com.socialactivityplanner.community_service.entity.Group;

public final class GroupMapper {

    private GroupMapper() {
    }

    public static GroupResponse toResponse(Group group) {
        return new GroupResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getCreatedBy().getId(),
                group.getCreatedAt(),
                group.getUpdatedAt()
        );
    }
}
