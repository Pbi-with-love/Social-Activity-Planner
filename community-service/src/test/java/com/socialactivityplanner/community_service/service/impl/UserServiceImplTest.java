package com.socialactivityplanner.community_service.service.impl;

import com.socialactivityplanner.community_service.dto.request.UserUpdateRequest;
import com.socialactivityplanner.community_service.dto.response.UserResponse;
import com.socialactivityplanner.community_service.entity.User;
import com.socialactivityplanner.community_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  private UserServiceImpl userService;
  private User user;
  private UUID userId;

  @BeforeEach
  void setUp() {
    userService = new UserServiceImpl(userRepository, passwordEncoder);
    userId = UUID.randomUUID();
    user = new User("existing_user", "existing@example.com", "hashed-password");
    ReflectionTestUtils.setField(user, "id", userId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
  }

  @Test
  void updateUserByUserIdPreservesFieldsOmittedFromPatch() {
    UserUpdateRequest request = new UserUpdateRequest(null, null);

    UserResponse response = userService.updateUserByUserId(userId, request);

    assertEquals("existing_user", user.getUserName());
    assertEquals("existing@example.com", user.getEmail());
    assertEquals("existing_user", response.userName());
    assertEquals("existing@example.com", response.email());
    verify(userRepository, never()).existsByEmail(any());
    verify(userRepository, never()).existsByUserName(any());
  }

  @Test
  void updateUserByUserIdUpdatesFieldsProvidedByPatch() {
    UserUpdateRequest request = new UserUpdateRequest("updated_user", "updated@example.com");
    when(userRepository.existsByEmail(request.email())).thenReturn(false);
    when(userRepository.existsByUserName(request.userName())).thenReturn(false);

    UserResponse response = userService.updateUserByUserId(userId, request);

    assertEquals("updated_user", response.userName());
    assertEquals("updated@example.com", response.email());
    verify(userRepository).existsByEmail(request.email());
    verify(userRepository).existsByUserName(request.userName());
  }
}
