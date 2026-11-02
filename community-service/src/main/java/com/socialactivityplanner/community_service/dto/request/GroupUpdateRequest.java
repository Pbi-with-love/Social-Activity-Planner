package com.socialactivityplanner.community_service.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GroupUpdateRequest(
    @NotBlank @Size(min = 1, max = 100) String name,
    String description) {
  @AssertTrue(message = "At least one field must be provided")
  public boolean hasAtLeastOneField() {
    return name != null || description != null;
  }
}
