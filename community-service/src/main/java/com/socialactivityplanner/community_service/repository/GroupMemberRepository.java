package com.socialactivityplanner.community_service.repository;

import com.socialactivityplanner.community_service.entity.GroupMember;
import com.socialactivityplanner.community_service.entity.GroupMemberId;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {

  List<GroupMember> findByIdGroupId(UUID groupId);

  List<GroupMember> findByIdUserId(UUID userId);

  boolean existsByIdGroupIdAndIdUserId(UUID groupId, UUID userId);
}
