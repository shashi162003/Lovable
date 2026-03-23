package com.devshashi.lovable.service.impl;

import com.devshashi.lovable.dto.project.FileContentResponse;
import com.devshashi.lovable.dto.project.FileNode;
import com.devshashi.lovable.service.FileService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public List<FileNode> getFileTree(Long projectId, Long userId) {
        return List.of();
    }

    @Override
    public FileContentResponse getFileContent(Long projectId, String path, Long userId) {
        return null;
    }
}
