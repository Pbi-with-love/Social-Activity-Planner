package com.socialactivityplanner.community_service.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "activity_group")
public class ActivityGroup {

  @Id
  @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
  private UUID id;

  @Column(nullable = false, length = 100)
  private String name;

  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by", nullable = false)
  private User createdBy;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "conversation_id", nullable = false, unique = true)
  private Conversation conversation;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  public ActivityGroup(String name, String description, User createdBy, Conversation conversation) {
    this.name = Objects.requireNonNull(name, "name must not be null");
    this.description = description;
    this.createdBy = Objects.requireNonNull(createdBy, "createdBy must not be null");
    this.conversation = Objects.requireNonNull(conversation, "conversation must not be null");
  }

  /** Updates only the fields that are provided (non-null). */
  public void update(String name, String description) {
    if (name != null) {
      this.name = name;
    }
    if (description != null) {
      this.description = description;
    }
  }
}
