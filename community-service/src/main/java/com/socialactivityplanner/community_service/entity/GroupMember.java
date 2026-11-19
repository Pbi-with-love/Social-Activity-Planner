package com.socialactivityplanner.community_service.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "group_member")
public class GroupMember {
  @EmbeddedId private GroupMemberId id;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("groupId")
  @JoinColumn(name = "group_id", nullable = false)
  private ActivityGroup group;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("userId")
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false, length = 10)
  private GroupRole role;

  @CreationTimestamp
  @Column(name = "joined_at", nullable = false, updatable = false)
  private Instant joinedAt;

  public GroupMember(ActivityGroup group, User user, GroupRole role) {
    this.group = Objects.requireNonNull(group, "group must not be null");
    this.user = Objects.requireNonNull(user, "user must not be null");
    this.role = Objects.requireNonNull(role, "role must not be null");
    this.id = new GroupMemberId(group.getId(), user.getId());
  }

  public void changeRole(GroupRole role) {
    this.role = Objects.requireNonNull(role, "role must not be null");
  }
}
