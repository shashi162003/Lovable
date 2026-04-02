package com.devshashi.lovable.service;

import com.devshashi.lovable.dto.chat.ChatResponse;

import java.util.List;

public interface ChatService {

    List<ChatResponse> getProjectChatHistory(Long projectId);

}
