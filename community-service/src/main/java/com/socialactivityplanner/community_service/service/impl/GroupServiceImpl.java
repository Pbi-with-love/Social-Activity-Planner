package com.socialactivityplanner.community_service.service.impl;

import com.socialactivityplanner.community_service.service.GroupService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.socialactivityplanner.community_service.dto.request.GroupMemberRoleUpdateRequest;
import com.socialactivityplanner.community_service.dto.request.GroupMemberAddRequest;
import com.socialactivityplanner.community_service.dto.response.GroupMemberResponse;
import com.socialactivityplanner.community_service.dto.request.GroupUpdateRequest;
import com.socialactivityplanner.community_service.dto.response.GroupResponse;
import com.socialactivityplanner.community_service.repository.GroupMemberRepository;
import com.socialactivityplanner.community_service.repository.GroupRepository;
import com.socialactivityplanner.community_service.dto.request.GroupCreateRequest;
import com.socialactivityplanner.community_service.entity.Group;
import com.socialactivityplanner.community_service.entity.GroupMember;
import com.socialactivityplanner.community_service.entity.GroupMemberId;
import com.socialactivityplanner.community_service.entity.GroupRole;
import com.socialactivityplanner.community_service.repository.UserRepository;
import com.socialactivityplanner.community_service.entity.User;
import com.socialactivityplanner.community_service.exception.*;
import com.socialactivityplanner.community_service.mapper.GroupMapper;

import java.util.UUID;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupServiceImpl implements GroupService {
  private final GroupRepository groupRepository;
  private final UserRepository userRepository;
  private final GroupMemberRepository groupMemberRepository;

  @Override
  @Transactional
  public GroupResponse createGroup(UUID userId, GroupCreateRequest request) {
    if (userId == null) {
      throw new IllegalArgumentException("User ID cannot be null");
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " does not exist"));

    Group group = new Group();
    group.setName(request.name());
    group.setDescription(request.description());
    group.setCreatedBy(user);

    return GroupMapper.toResponse(groupRepository.save(group));
  }

  @Override
  public GroupResponse getGroupById(UUID groupId) {
    Group group = groupRepository.findById(groupId)
        .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

    return GroupMapper.toResponse(group);
  }

  @Override
  @Transactional
  public GroupResponse updateGroup(UUID groupId, UUID userId, GroupUpdateRequest request) {
    Group group = groupRepository.findById(groupId)
        .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

    GroupMember member = groupMemberRepository.findById(new GroupMemberId(groupId, userId))
        .orElseThrow(() -> new AccessDeniedException(
            "You are not a member of this group"));

    if (member.getRole() != GroupRole.OWNER && member.getRole() != GroupRole.ADMIN) {
      throw new AccessDeniedException(
          "You do not have permission to update this group");
    }

    if (request.name() != null) {
      group.setName(request.name());
    }

    if (request.description() != null) {
      group.setDescription(request.description());
    }

    return GroupMapper.toResponse(group);
  }

  @Override
  public void deleteGroup(UUID groupId, UUID userId) {

  }

  @Override
  public GroupMemberResponse addMember(UUID groupId, UUID userId, GroupMemberAddRequest request) {

  }

  @Override
  public List<GroupMemberResponse> getMembers(UUID groupId) {

  }

  @Override
  public GroupMemberResponse updateMemberRole(UUID groupId, UUID memberId, UUID userId,
      GroupMemberRoleUpdateRequest request) {

  }

  @Override
  public void removeMember(UUID groupId, UUID memberId, UUID userId) {
}
