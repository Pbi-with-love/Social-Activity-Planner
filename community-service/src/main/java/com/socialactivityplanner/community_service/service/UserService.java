package com.socialactivityplanner.community_service.service;

import com.socialactivityplanner.community_service.dto.request.UserCreateRequest;
import com.socialactivityplanner.community_service.dto.response.UserResponse;

import java.util.UUID;

public interface UserService {
    UserResponse createUser(UserCreateRequest request);
    UserResponse getUserByUserId(UUID userId);
    UserResponse updateUserByUserId(UUID userId, UserCreateRequest request);
}