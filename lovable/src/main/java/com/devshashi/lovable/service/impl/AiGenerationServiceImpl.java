package com.devshashi.lovable.service.impl;

import com.devshashi.lovable.service.AiGenerationService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class AiGenerationServiceImpl implements AiGenerationService {

    @Override
    public Flux<String> streamResponse(String message, Long projectId) {
        return null;
    }
}
