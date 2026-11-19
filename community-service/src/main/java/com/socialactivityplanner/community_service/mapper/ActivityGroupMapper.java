package com.socialactivityplanner.community_service.mapper;

import com.socialactivityplanner.community_service.dto.response.ActivityGroupResponse;
import com.socialactivityplanner.community_service.entity.ActivityGroup;

public final class ActivityGroupMapper {

  private ActivityGroupMapper() {}

  public static ActivityGroupResponse toResponse(ActivityGroup group) {
    return new ActivityGroupResponse(
        group.getId(),
        group.getName(),
        group.getDescription(),
        group.getCreatedBy().getId(),
        group.getCreatedAt(),
        group.getUpdatedAt());
  }
}
