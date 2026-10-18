package com.socialactivityplanner.community_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GroupUpdateRequest(
    @NotBlank @Size(min = 1, max = 100) String name,
    String description) {
}
