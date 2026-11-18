package com.socialactivityplanner.community_service.service;

import com.socialactivityplanner.community_service.dto.request.GroupCreateRequest;
import com.socialactivityplanner.community_service.dto.request.GroupMemberAddRequest;
import com.socialactivityplanner.community_service.dto.request.GroupMemberRoleUpdateRequest;
import com.socialactivityplanner.community_service.dto.request.GroupUpdateRequest;
import com.socialactivityplanner.community_service.dto.response.GroupMemberResponse;
import com.socialactivityplanner.community_service.dto.response.GroupResponse;

import java.util.List;
import java.util.UUID;

public interface GroupService {

  GroupResponse createGroup(UUID userId, GroupCreateRequest request);

  GroupResponse getGroupById(UUID groupId);

  GroupResponse updateGroup(UUID groupId, UUID userId, GroupUpdateRequest request);

  void deleteGroup(UUID groupId, UUID userId);

  GroupMemberResponse addMember(UUID groupId, UUID userId, GroupMemberAddRequest request);

  List<GroupMemberResponse> getMembers(UUID groupId);

  GroupMemberResponse updateMemberRole(UUID groupId, UUID memberId, UUID userId,
      GroupMemberRoleUpdateRequest request);

  void removeMember(UUID groupId, UUID memberId, UUID userId);
}
