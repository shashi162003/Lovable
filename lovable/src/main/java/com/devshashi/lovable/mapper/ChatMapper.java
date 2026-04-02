package com.devshashi.lovable.mapper;

import com.devshashi.lovable.dto.chat.ChatResponse;
import com.devshashi.lovable.entity.ChatMessage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    List<ChatResponse> fromListOfChatMessage(List<ChatMessage> chatMessageList);

}
