package com.socialactivityplanner.community_service.mapper;

import com.socialactivityplanner.community_service.dto.response.GroupMemberResponse;
import com.socialactivityplanner.community_service.entity.GroupMember;

public final class GroupMemberMapper {

    private GroupMemberMapper() {
    }

    public static GroupMemberResponse toResponse(GroupMember member) {
        return new GroupMemberResponse(
                member.getGroup().getId(),
                member.getUser().getId(),
                member.getUser().getName(),
                member.getRole(),
                member.getJoinedAt()
        );
    }
}
