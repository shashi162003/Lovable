package com.devshashi.distributed_lovable.workspace_service.consumer;

import com.devshashi.distributed_lovable.common_lib.event.FileStoreRequestEvent;
import com.devshashi.distributed_lovable.common_lib.event.FileStoreResponseEvent;
import com.devshashi.distributed_lovable.workspace_service.entity.ProcessedEvent;
import com.devshashi.distributed_lovable.workspace_service.repository.ProcessedEventRepository;
import com.devshashi.distributed_lovable.workspace_service.service.ProjectFileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageConsumer {

    private final ProjectFileService projectFileService;
    private final ProcessedEventRepository processedEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    @KafkaListener(topics = "file-storage-request-event", groupId = "workspace-group")
    public void consumeFileEvent(FileStoreRequestEvent requestEvent){

        // Idempotency check
        if(processedEventRepository.existsById(requestEvent.sagaId())){
            log.info("Duplicate Saga detected: {}. Resending previous ACK.", requestEvent.sagaId());
            sendResponse(requestEvent, true, null);
            return;
        }

        try{
            log.info("Saving file: {}", requestEvent.filePath());
            projectFileService.saveFile(requestEvent.projectId(), requestEvent.filePath(), requestEvent.content());
            processedEventRepository.save(new ProcessedEvent(
                    requestEvent.sagaId(), LocalDateTime.now()
            ));
            sendResponse(requestEvent, true, null);
        } catch (Exception e){
            log.error("Error saving file: {}", e.getMessage());
            sendResponse(requestEvent, false, e.getMessage());
        }
    }

    private void sendResponse(FileStoreRequestEvent requestEvent, boolean success, String error) {
        FileStoreResponseEvent response = FileStoreResponseEvent.builder()
                .sagaId(requestEvent.sagaId())
                .projectId(requestEvent.projectId())
                .success(success)
                .errorMessage(error)
                .build();

        kafkaTemplate.send("file-store-responses", response);
    }

}
