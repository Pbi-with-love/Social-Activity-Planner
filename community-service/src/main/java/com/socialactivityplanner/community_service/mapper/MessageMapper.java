package com.socialactivityplanner.community_service.mapper;

import com.socialactivityplanner.community_service.dto.response.MessageResponse;
import com.socialactivityplanner.community_service.entity.Message;
import com.socialactivityplanner.community_service.entity.User;

public final class MessageMapper {

  private MessageMapper() {}

  public static MessageResponse toResponse(Message message) {
    User sender = message.getSender();
    return new MessageResponse(
        message.getId(),
        message.getConversation().getId(),
        sender == null ? null : sender.getId(),
        sender == null ? null : sender.getUserName(),
        message.getContent(),
        message.getSentAt(),
        message.getUpdatedAt());
  }
}
