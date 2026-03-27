package com.devshashi.lovable.controller;

import com.devshashi.lovable.dto.chat.ChatRequest;
import com.devshashi.lovable.service.AiGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final AiGenerationService aiGenerationService;

    @PostMapping(value = "/api/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChat(
            @RequestBody ChatRequest request
    ) {
        return aiGenerationService.streamResponse(request.message(), request.projectId())
                .map(data -> ServerSentEvent.<String>builder()
                        .data(data)
                        .build());
    }

}
