package com.socialactivityplanner.community_service.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MessageCreateRequest(
        @NotBlank
        String content
) {}
