package com.devshashi.lovable.service;

import com.devshashi.lovable.dto.project.FileContentResponse;
import com.devshashi.lovable.dto.project.FileNode;

import java.util.List;

public interface ProjectFileService {
    List<FileNode> getFileTree(Long projectId);

    FileContentResponse getFileContent(Long projectId, String path);

    void saveFile(Long projectId, String filePath, String fileContent);
}
