package com.devshashi.lovable.service.impl;

import com.devshashi.lovable.dto.chat.ChatResponse;
import com.devshashi.lovable.entity.ChatMessage;
import com.devshashi.lovable.entity.ChatSession;
import com.devshashi.lovable.entity.ChatSessionId;
import com.devshashi.lovable.mapper.ChatMapper;
import com.devshashi.lovable.repository.ChatMessageRepository;
import com.devshashi.lovable.repository.ChatSessionRepository;
import com.devshashi.lovable.security.AuthUtil;
import com.devshashi.lovable.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final AuthUtil authUtil;
    private final ChatMapper chatMapper;

    @Override
    public List<ChatResponse> getProjectChatHistory(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        ChatSession chatSession = chatSessionRepository.getReferenceById(
                new ChatSessionId(projectId, userId)
        );
        List<ChatMessage> chatMessageList = chatMessageRepository.findByChatSession(chatSession);
        return chatMapper.fromListOfChatMessage(chatMessageList);
    }
}
