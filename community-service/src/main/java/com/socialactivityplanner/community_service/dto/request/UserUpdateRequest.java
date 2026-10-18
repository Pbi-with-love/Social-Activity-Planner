package com.socialactivityplanner.community_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @Size(min = 2, max = 25) @Pattern(regexp = "^[a-zA-Z0-9_]+$") String userName,
    @Email String email,
    @Size(min = 1, max = 50) String firstName,
    @Size(min = 1, max = 50) String lastName) {
}
