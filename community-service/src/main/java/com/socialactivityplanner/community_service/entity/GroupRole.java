package com.socialactivityplanner.community_service.entity;

import jakarta.persistence.Converter;

public enum GroupRole {
  OWNER, ADMIN, MEMBER;

  @Converter(autoApply = true)
  public static class DbConverter extends LowercaseEnumConverter<GroupRole> {
    public DbConverter() {
      super(GroupRole.class);
    }
  }
}
