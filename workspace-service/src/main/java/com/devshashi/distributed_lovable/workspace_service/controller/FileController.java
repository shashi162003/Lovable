package com.devshashi.distributed_lovable.workspace_service.controller;

import com.devshashi.distributed_lovable.workspace_service.dto.project.FileContentResponse;
import com.devshashi.distributed_lovable.workspace_service.dto.project.FileTreeResponse;
import com.devshashi.distributed_lovable.workspace_service.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/files")
public class FileController {

    private final ProjectFileService fileService;

    @GetMapping
    public ResponseEntity<FileTreeResponse> getFileTree(@PathVariable Long projectId){
        Long userId = 1L;
        return ResponseEntity.ok(fileService.getFileTree(projectId));
    }

    @GetMapping("/content")
    public ResponseEntity<FileContentResponse> getFile(
            @PathVariable Long projectId,
            @RequestParam String path
    ) {
        return ResponseEntity.ok(fileService.getFileContent(projectId, path));
    }
}
