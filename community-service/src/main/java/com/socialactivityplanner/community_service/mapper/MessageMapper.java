package com.socialactivityplanner.community_service.mapper;

import com.socialactivityplanner.community_service.dto.response.MessageResponse;
import com.socialactivityplanner.community_service.entity.Message;

public final class MessageMapper {

    private MessageMapper() {
    }

    public static MessageResponse toResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getGroup().getId(),
                message.getSender().getId(),
                message.getSender().getUserName(),
                message.getContent(),
                message.getSentAt(),
                message.getUpdatedAt()
        );
    }
}
