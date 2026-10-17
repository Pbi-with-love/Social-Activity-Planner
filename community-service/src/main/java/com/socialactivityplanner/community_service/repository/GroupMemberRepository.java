package com.socialactivityplanner.community_service.repository;

import com.socialactivityplanner.community_service.entity.GroupMember;
import com.socialactivityplanner.community_service.entity.GroupMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {

    List<GroupMember> findByIdGroupId(UUID groupId);

    List<GroupMember> findByIdUserId(UUID userId);

    boolean existsByIdGroupIdAndIdUserId(UUID groupId, UUID userId);
}

