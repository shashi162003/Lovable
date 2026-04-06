package com.devshashi.distributed_lovable.workspace_service.service;

import com.devshashi.distributed_lovable.common_lib.dto.FileTreeDTO;
import com.devshashi.distributed_lovable.workspace_service.dto.project.FileContentResponse;

public interface ProjectFileService {
    FileTreeDTO getFileTree(Long projectId);

    String getFileContent(Long projectId, String path);

    void saveFile(Long projectId, String filePath, String fileContent);
}
