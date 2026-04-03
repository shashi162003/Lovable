package com.devshashi.lovable.service;

import com.devshashi.lovable.dto.chat.StreamResponse;
import reactor.core.publisher.Flux;

import java.util.Optional;

public interface AiGenerationService {
    Flux<StreamResponse> streamResponse(String message, Long projectId);
}
