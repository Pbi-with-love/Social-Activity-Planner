package com.socialactivityplanner.community_service.service;

import com.socialactivityplanner.community_service.dto.request.ActivityGroupCreateRequest;
import com.socialactivityplanner.community_service.dto.request.ActivityGroupUpdateRequest;
import com.socialactivityplanner.community_service.dto.request.GroupMemberAddRequest;
import com.socialactivityplanner.community_service.dto.request.GroupMemberRoleUpdateRequest;
import com.socialactivityplanner.community_service.dto.response.ActivityGroupResponse;
import com.socialactivityplanner.community_service.dto.response.GroupMemberResponse;
import java.util.List;
import java.util.UUID;

public interface ActivityGroupService {

  ActivityGroupResponse createGroup(UUID userId, ActivityGroupCreateRequest request);

  ActivityGroupResponse getGroupById(UUID groupId);

  ActivityGroupResponse updateGroup(UUID groupId, UUID userId, ActivityGroupUpdateRequest request);

  void deleteGroup(UUID groupId, UUID userId);

  GroupMemberResponse addMember(UUID groupId, UUID userId, GroupMemberAddRequest request);

  List<GroupMemberResponse> getMembers(UUID groupId);

  GroupMemberResponse updateMemberRole(
      UUID groupId, UUID memberId, UUID userId, GroupMemberRoleUpdateRequest request);

  void removeMember(UUID groupId, UUID memberId, UUID userId);
}
