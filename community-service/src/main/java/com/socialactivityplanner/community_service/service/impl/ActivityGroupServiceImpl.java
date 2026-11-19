package com.socialactivityplanner.community_service.service.impl;

import com.socialactivityplanner.community_service.dto.request.ActivityGroupCreateRequest;
import com.socialactivityplanner.community_service.dto.request.ActivityGroupUpdateRequest;
import com.socialactivityplanner.community_service.dto.request.GroupMemberAddRequest;
import com.socialactivityplanner.community_service.dto.request.GroupMemberRoleUpdateRequest;
import com.socialactivityplanner.community_service.dto.response.ActivityGroupResponse;
import com.socialactivityplanner.community_service.dto.response.GroupMemberResponse;
import com.socialactivityplanner.community_service.entity.ActivityGroup;
import com.socialactivityplanner.community_service.entity.Conversation;
import com.socialactivityplanner.community_service.entity.ConversationType;
import com.socialactivityplanner.community_service.entity.GroupMember;
import com.socialactivityplanner.community_service.entity.GroupMemberId;
import com.socialactivityplanner.community_service.entity.GroupRole;
import com.socialactivityplanner.community_service.entity.User;
import com.socialactivityplanner.community_service.exception.AlreadyMemberException;
import com.socialactivityplanner.community_service.exception.GroupAccessDeniedException;
import com.socialactivityplanner.community_service.exception.ResourceNotFoundException;
import com.socialactivityplanner.community_service.mapper.ActivityGroupMapper;
import com.socialactivityplanner.community_service.mapper.GroupMemberMapper;
import com.socialactivityplanner.community_service.repository.ActivityGroupRepository;
import com.socialactivityplanner.community_service.repository.ConversationRepository;
import com.socialactivityplanner.community_service.repository.GroupMemberRepository;
import com.socialactivityplanner.community_service.repository.UserRepository;
import com.socialactivityplanner.community_service.service.ActivityGroupService;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityGroupServiceImpl implements ActivityGroupService {

  private static final Set<GroupRole> OWNER_ONLY = EnumSet.of(GroupRole.OWNER);
  private static final Set<GroupRole> MANAGER_ROLES = EnumSet.of(GroupRole.OWNER, GroupRole.ADMIN);

  private final ActivityGroupRepository groupRepository;
  private final ConversationRepository conversationRepository;
  private final UserRepository userRepository;
  private final GroupMemberRepository groupMemberRepository;

  @Override
  @Transactional
  public ActivityGroupResponse createGroup(UUID userId, ActivityGroupCreateRequest request) {
    Objects.requireNonNull(userId, "userId must not be null");

    User user = findUserOrThrow(userId);

    Conversation conversation =
        conversationRepository.save(new Conversation(ConversationType.GROUP));
    ActivityGroup group =
        new ActivityGroup(request.name(), request.description(), user, conversation);
    ActivityGroup saved = groupRepository.save(group);

    groupMemberRepository.save(new GroupMember(saved, user, GroupRole.OWNER));

    return ActivityGroupMapper.toResponse(saved);
  }

  @Override
  public ActivityGroupResponse getGroupById(UUID groupId) {
    return ActivityGroupMapper.toResponse(findGroupOrThrow(groupId));
  }

  @Override
  @Transactional
  public ActivityGroupResponse updateGroup(
      UUID groupId, UUID userId, ActivityGroupUpdateRequest request) {
    ActivityGroup group = findGroupOrThrow(groupId);
    requireRole(groupId, userId, MANAGER_ROLES, "update");

    group.update(request.name(), request.description());

    return ActivityGroupMapper.toResponse(group);
  }

  @Override
  @Transactional
  public void deleteGroup(UUID groupId, UUID userId) {
    ActivityGroup group = findGroupOrThrow(groupId);
    requireRole(groupId, userId, OWNER_ONLY, "delete");

    // Members are removed through JPA (not only ON DELETE CASCADE) because requireRole loaded the
    // caller's
    // membership into the persistence context; Hibernate refuses to flush it pointing at a deleted
    // group.
    groupMemberRepository.deleteAll(groupMemberRepository.findByIdGroupId(groupId));
    // The group references the conversation, so it goes first; messages cascade from the
    // conversation.
    groupRepository.delete(group);
    conversationRepository.delete(group.getConversation());
  }

  @Override
  @Transactional
  public GroupMemberResponse addMember(UUID groupId, UUID userId, GroupMemberAddRequest request) {
    ActivityGroup group = findGroupOrThrow(groupId);
    GroupMember caller = requireRole(groupId, userId, MANAGER_ROLES, "add members to");

    GroupRole role = request.role() != null ? request.role() : GroupRole.MEMBER;
    if (role == GroupRole.OWNER) {
      throw new GroupAccessDeniedException("A group can only have the owner it was created with");
    }
    if (role == GroupRole.ADMIN && caller.getRole() != GroupRole.OWNER) {
      throw new GroupAccessDeniedException("Only the group owner can add admins");
    }

    User newMember = findUserOrThrow(request.userId());
    if (groupMemberRepository.existsByIdGroupIdAndIdUserId(groupId, newMember.getId())) {
      throw new AlreadyMemberException("User is already a member of this group");
    }

    GroupMember saved = groupMemberRepository.save(new GroupMember(group, newMember, role));
    return GroupMemberMapper.toResponse(saved);
  }

  @Override
  public List<GroupMemberResponse> getMembers(UUID groupId) {
    if (!groupRepository.existsById(groupId)) {
      throw new ResourceNotFoundException("Group not found with id: " + groupId);
    }

    return groupMemberRepository.findByIdGroupId(groupId).stream()
        .map(GroupMemberMapper::toResponse)
        .toList();
  }

  @Override
  @Transactional
  public GroupMemberResponse updateMemberRole(
      UUID groupId, UUID memberId, UUID userId, GroupMemberRoleUpdateRequest request) {
    findGroupOrThrow(groupId);
    requireRole(groupId, userId, OWNER_ONLY, "change member roles in");

    GroupMember target = findMemberOrThrow(groupId, memberId);
    if (target.getRole() == GroupRole.OWNER) {
      throw new GroupAccessDeniedException("The role of the group owner cannot be changed");
    }
    if (request.role() == GroupRole.OWNER) {
      throw new GroupAccessDeniedException("Transferring group ownership is not supported");
    }

    target.changeRole(request.role());
    return GroupMemberMapper.toResponse(target);
  }

  @Override
  @Transactional
  public void removeMember(UUID groupId, UUID memberId, UUID userId) {
    findGroupOrThrow(groupId);
    GroupMember caller = requireMember(groupId, userId);
    GroupMember target = findMemberOrThrow(groupId, memberId);

    if (target.getRole() == GroupRole.OWNER) {
      throw new GroupAccessDeniedException(
          "The group owner cannot be removed; delete the group instead");
    }

    boolean leavingSelf = memberId.equals(userId);
    if (!leavingSelf) {
      if (!MANAGER_ROLES.contains(caller.getRole())) {
        throw new GroupAccessDeniedException(
            "You do not have permission to remove members from this group");
      }
      if (target.getRole() == GroupRole.ADMIN && caller.getRole() != GroupRole.OWNER) {
        throw new GroupAccessDeniedException("Only the group owner can remove an admin");
      }
    }

    groupMemberRepository.delete(target);
  }

  // --- helpers ---

  private ActivityGroup findGroupOrThrow(UUID groupId) {
    return groupRepository
        .findById(groupId)
        .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));
  }

  private User findUserOrThrow(UUID userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
  }

  private GroupMember findMemberOrThrow(UUID groupId, UUID memberId) {
    return groupMemberRepository
        .findById(new GroupMemberId(groupId, memberId))
        .orElseThrow(() -> new ResourceNotFoundException("Member not found in group: " + memberId));
  }

  private GroupMember requireMember(UUID groupId, UUID userId) {
    return groupMemberRepository
        .findById(new GroupMemberId(groupId, userId))
        .orElseThrow(() -> new GroupAccessDeniedException("You are not a member of this group"));
  }

  private GroupMember requireRole(
      UUID groupId, UUID userId, Set<GroupRole> allowed, String action) {
    GroupMember member = requireMember(groupId, userId);
    if (!allowed.contains(member.getRole())) {
      throw new GroupAccessDeniedException(
          "You do not have permission to " + action + " this group");
    }
    return member;
  }
}
