package com.devshashi.lovable.dto.chat;

import com.devshashi.lovable.entity.ChatEvent;
import com.devshashi.lovable.entity.ChatSession;
import com.devshashi.lovable.enums.MessageRole;

import java.time.Instant;
import java.util.List;

public record ChatResponse(
        Long id,
        MessageRole role,
        List<ChatEventResponse> events,
        String content,
        Integer tokensUsed,
        Instant createdAt
) {
}
