package com.socialactivityplanner.community_service.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

// type, batch_id and deleted_at are not mapped yet: the DB defaults type to 'text' and nothing uses the others
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "message")
public class Message {

  @Id
  @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "conversation_id", nullable = false)
  private Conversation conversation;

  /** Null for system messages. */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sender_id")
  private User sender;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @CreationTimestamp
  @Column(name = "sent_at", nullable = false, updatable = false)
  private Instant sentAt;

  /** Null until the message is edited. */
  @Column(name = "updated_at")
  private Instant updatedAt;

  public Message(Conversation conversation, User sender, String content) {
    this.conversation = Objects.requireNonNull(conversation, "conversation must not be null");
    this.sender = Objects.requireNonNull(sender, "sender must not be null");
    this.content = Objects.requireNonNull(content, "content must not be null");
  }

  public void editContent(String content) {
    this.content = Objects.requireNonNull(content, "content must not be null");
    this.updatedAt = Instant.now();
  }
}
