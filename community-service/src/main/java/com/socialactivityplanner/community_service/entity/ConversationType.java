package com.socialactivityplanner.community_service.entity;

import jakarta.persistence.Converter;

public enum ConversationType {
  GROUP, DIRECT;

  @Converter(autoApply = true)
  public static class DbConverter extends LowercaseEnumConverter<ConversationType> {
    public DbConverter() {
      super(ConversationType.class);
    }
  }
}
