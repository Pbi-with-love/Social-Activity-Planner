package com.socialactivityplanner.community_service.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ActivityGroupUpdateRequest(
    @Pattern(regexp = ".*\\S.*", message = "must not be blank") @Size(max = 100) String name,
    String description) {
  @AssertTrue(message = "At least one field must be provided")
  public boolean hasAtLeastOneField() {
    return name != null || description != null;
  }
}
