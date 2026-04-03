package com.devshashi.lovable.service.impl;

import com.devshashi.lovable.dto.chat.StreamResponse;
import com.devshashi.lovable.entity.*;
import com.devshashi.lovable.enums.ChatEventType;
import com.devshashi.lovable.enums.MessageRole;
import com.devshashi.lovable.error.ResourceNotFoundException;
import com.devshashi.lovable.llm.LlmResponseParser;
import com.devshashi.lovable.llm.PromptUtils;
import com.devshashi.lovable.llm.advisors.FileTreeContextAdvisor;
import com.devshashi.lovable.llm.tools.CodeGenerationTools;
import com.devshashi.lovable.repository.*;
import com.devshashi.lovable.security.AuthUtil;
import com.devshashi.lovable.service.AiGenerationService;
import com.devshashi.lovable.service.ProjectFileService;
import com.devshashi.lovable.service.UsageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiGenerationServiceImpl implements AiGenerationService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ChatClient chatClient;
    private final AuthUtil authUtil;
    private final ProjectFileService projectFileService;
    private final FileTreeContextAdvisor fileTreeContextAdvisor;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final LlmResponseParser llmResponseParser;
    private final ChatEventRepository chatEventRepository;
    private final UsageService usageService;

    private static final Pattern FILE_TAG_PATTERN = Pattern.compile("<file path=\"([^\"]+)\">(.*?)</file>", Pattern.DOTALL);

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public Flux<StreamResponse> streamResponse(String userMessage, Long projectId) {
//        usageService.checkDailyTokensUsage();

        Long userId = authUtil.getCurrentUserId();
        ChatSession chatSession = createChatSessionIfNotExists(projectId, userId);

        Map<String, Object> advisorParams = Map.of(
                "userId", userId,
                "projectId", projectId
        );

        StringBuilder fullResponseBuffer = new StringBuilder();

        CodeGenerationTools codeGenerationTools = new CodeGenerationTools(projectFileService, projectId);

        AtomicReference<Long> startTime = new AtomicReference<>(System.currentTimeMillis());
        AtomicReference<Long> endTime = new AtomicReference<>(0L);
        AtomicReference<Usage> usageRef = new AtomicReference<>();

        return chatClient.prompt()
                .system(PromptUtils.CODE_GENERATION_SYSTEM_PROMPT)
                .user(userMessage)
                .tools(codeGenerationTools)
                .advisors(advisorSpec -> {
                    advisorSpec.params(advisorParams);
                    advisorSpec.advisors(fileTreeContextAdvisor);
                })
                .stream()
                .chatResponse()
                .doOnNext(response -> {
                    if (response.getResult() == null) return;

                    String content = response.getResult().getOutput().getText();

                    if(content != null && !content.isEmpty() && endTime.get() == 0){
                        endTime.set(System.currentTimeMillis());
                    }

                    if(response.getMetadata().getUsage() != null){
                        usageRef.set(response.getMetadata().getUsage());
                    }

                    if(content != null){
                        fullResponseBuffer.append(content);
                    }
                })
                .doOnComplete(() -> {
                    Schedulers.boundedElastic().schedule(() -> {
//                        parseAndSaveFiles(fullResponseBuffer.toString(), projectId);
                        long duration = (endTime.get() - startTime.get()) / 1000;
                        finalizeChats(userMessage, chatSession, fullResponseBuffer.toString(), duration, usageRef.get());
                    });
                })
                .doOnError(error -> log.error("Error during streaming for projectId: {}", projectId))
                .filter(response -> response.getResult() != null
                        && response.getResult().getOutput().getText() != null
                        && !response.getResult().getOutput().getText().isEmpty())
                .map(response -> {
                    String text = response.getResult().getOutput().getText();
                    return new StreamResponse(text != null ? text : "");
                });
    }

    private void finalizeChats(String userMessage, ChatSession chatSession, String fullText, Long duration, Usage usage){
        Long projectId = chatSession.getProject().getId();

        if(usage != null){
            int totalTokens = usage.getTotalTokens();
            usageService.recordTokenUsage(chatSession.getUser().getId(), totalTokens);
        }

        chatMessageRepository.save(
                ChatMessage.builder()
                        .chatSession(chatSession)
                        .role(MessageRole.USER)
                        .content(userMessage)
                        .tokensUsed(usage.getPromptTokens())
                        .build()
        );

        ChatMessage assistantChatMessage = ChatMessage.builder()
                .role(MessageRole.ASSISTANT)
                .content("Assistant Message here...")
                .chatSession(chatSession)
                .tokensUsed(usage.getCompletionTokens())
                .build();

        assistantChatMessage = chatMessageRepository.save(assistantChatMessage);

        List<ChatEvent> chatEventList = llmResponseParser.parseChatEvents(fullText, assistantChatMessage);
        chatEventList.addFirst(ChatEvent.builder()
                        .type(ChatEventType.THOUGHT)
                        .chatMessage(assistantChatMessage)
                        .content("Thought for " + duration + "s")
                        .sequenceOrder(0)
                .build());

        chatEventList.stream()
                .filter(e -> e.getType() == ChatEventType.FILE_EDIT)
                .forEach(e -> projectFileService.saveFile(projectId, e.getFilePath(), e.getContent()));

        chatEventRepository.saveAll(chatEventList);
    }

    private ChatSession createChatSessionIfNotExists(Long projectId, Long userId) {
        ChatSessionId chatSessionId = new ChatSessionId(projectId, userId);
        ChatSession chatSession = chatSessionRepository.findById(chatSessionId).orElse(null);
        if(chatSession == null){
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));
            chatSession = ChatSession.builder()
                    .id(chatSessionId)
                    .project(project)
                    .user(user)
                    .build();
            chatSession = chatSessionRepository.save(chatSession);
        }
        return chatSession;
    }
}
