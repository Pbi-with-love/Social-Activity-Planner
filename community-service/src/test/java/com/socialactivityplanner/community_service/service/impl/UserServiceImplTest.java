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
    user = new User();
    user.setId(userId);
    user.setUserName("existing_user");
    user.setFirstName("Existing");
    user.setLastName("User");
    user.setEmail("existing@example.com");
    user.setPasswordHash("hashed-password");
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
  }

  @Test
  void updateUserByUserIdPreservesFieldsOmittedFromPatch() {
    UserUpdateRequest request = new UserUpdateRequest(null, null, null, null);
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    UserResponse response = userService.updateUserByUserId(userId, request);

    assertEquals("existing_user", user.getUserName());
    assertEquals("Existing", user.getFirstName());
    assertEquals("User", user.getLastName());
    assertEquals("existing@example.com", user.getEmail());
    assertEquals("existing_user", response.userName());
    assertEquals("Existing", response.firstName());
    assertEquals("User", response.lastName());
    assertEquals("existing@example.com", response.email());
    verify(userRepository, never()).existsByEmail(any());
    verify(userRepository, never()).existsByUserName(any());
    verify(userRepository).save(user);
  }

  @Test
  void updateUserByUserIdUpdatesFieldsProvidedByPatch() {
    UserUpdateRequest request = new UserUpdateRequest(
        "updated_user", "updated@example.com", "Updated", "Name");
    when(userRepository.existsByEmail(request.email())).thenReturn(false);
    when(userRepository.existsByUserName(request.userName())).thenReturn(false);
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    UserResponse response = userService.updateUserByUserId(userId, request);

    assertEquals("updated_user", response.userName());
    assertEquals("Updated", response.firstName());
    assertEquals("Name", response.lastName());
    assertEquals("updated@example.com", response.email());
    verify(userRepository).existsByEmail(request.email());
    verify(userRepository).existsByUserName(request.userName());
  }
}
