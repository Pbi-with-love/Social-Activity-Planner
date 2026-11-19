package com.socialactivityplanner.community_service.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

  @Id
  @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
  private UUID id;

  @Column(name = "username", nullable = false, unique = true, length = 50)
  private String userName;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  public User(
      String userName,
      String email,
      String passwordHash) {
    this.userName = Objects.requireNonNull(userName, "userName must not be null");
    this.email = Objects.requireNonNull(email, "email must not be null");
    this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash must not be null");
  }

  /** Updates only the fields that are provided (non-null). */
  public void updateProfile(String userName, String email) {
    if (userName != null) {
      this.userName = userName;
    }
    if (email != null) {
      this.email = email;
    }
  }
}
