package com.devshashi.lovable.service;

import com.devshashi.lovable.dto.project.FileContentResponse;
import com.devshashi.lovable.dto.project.FileNode;

import java.util.List;

public interface FileService {
    List<FileNode> getFileTree(Long projectId, Long userId);

    FileContentResponse getFileContent(Long projectId, String path, Long userId);
}
