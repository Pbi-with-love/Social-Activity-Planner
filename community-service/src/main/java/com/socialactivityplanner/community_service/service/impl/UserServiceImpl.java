package com.socialactivityplanner.community_service.service.impl;

import com.socialactivityplanner.community_service.dto.request.UserCreateRequest;
import com.socialactivityplanner.community_service.dto.response.UserResponse;
import com.socialactivityplanner.community_service.entity.User;
import com.socialactivityplanner.community_service.exception.DuplicateResourceException;
import com.socialactivityplanner.community_service.exception.ResourceNotFoundException;
import com.socialactivityplanner.community_service.mapper.UserMapper;
import com.socialactivityplanner.community_service.repository.UserRepository;
import com.socialactivityplanner.community_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // --- POST /api/v1/users ---
    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already in use: " + request.email());
        }
        if (userRepository.existsByUserName(request.userName())) {
            throw new DuplicateResourceException("Username already taken: " + request.userName());
        }

        User user = new User();
        user.setUserName(request.userName());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        return UserMapper.toResponse(userRepository.save(user));
    }

    // --- GET /api/v1/users/me ---

    @Override
    public UserResponse getUserByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return UserMapper.toResponse(user);
    }

    // --- PATCH /api/v1/users/me ---

    @Override
    @Transactional
    public UserResponse updateUserByUserId(UUID userId, UserCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (!user.getEmail().equals(request.email())
                && userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already in use: " + request.email());
        }
        if (!user.getUserName().equals(request.userName())
                && userRepository.existsByUserName(request.userName())) {
            throw new DuplicateResourceException("Username already taken: " + request.userName());
        }

        user.setUserName(request.userName());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        return UserMapper.toResponse(userRepository.save(user));
    }
}