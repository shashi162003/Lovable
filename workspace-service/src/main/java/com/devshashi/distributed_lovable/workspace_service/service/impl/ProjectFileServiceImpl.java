package com.devshashi.distributed_lovable.workspace_service.service.impl;

import com.devshashi.distributed_lovable.common_lib.dto.FileNode;
import com.devshashi.distributed_lovable.common_lib.dto.FileTreeDTO;
import com.devshashi.distributed_lovable.common_lib.error.ResourceNotFoundException;
import com.devshashi.distributed_lovable.workspace_service.dto.project.FileContentResponse;
import com.devshashi.distributed_lovable.workspace_service.entity.Project;
import com.devshashi.distributed_lovable.workspace_service.entity.ProjectFile;
import com.devshashi.distributed_lovable.workspace_service.mapper.ProjectFileMapper;
import com.devshashi.distributed_lovable.workspace_service.repository.ProjectFileRepository;
import com.devshashi.distributed_lovable.workspace_service.repository.ProjectRepository;
import com.devshashi.distributed_lovable.workspace_service.service.ProjectFileService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectFileServiceImpl implements ProjectFileService {

    private final ProjectRepository projectRepository;
    private final ProjectFileRepository projectFileRepository;
    private final MinioClient minioClient;
    private final ProjectFileMapper projectFileMapper;

    @Value("${minio.project-bucket}")
    private String projectBucket;

//    private static final String BUCKET_NAME = "projects";

    @Override
    public FileTreeDTO getFileTree(Long projectId) {
        List<ProjectFile> projectFileList = projectFileRepository.findByProjectId(projectId);
        List<FileNode> projectFileNodes =  projectFileMapper.toListOfFileNode(projectFileList);
        return new FileTreeDTO(projectFileNodes);
    }

    @Override
    public String getFileContent(Long projectId, String path) {
        String objectName = projectId + "/" + path;

        try{
            InputStream is = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(projectBucket)
                            .object(objectName)
                            .build()
            );
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e){
            log.error("Failed to read file: {}/{}", projectId, path, e);
            throw new RuntimeException("Failed to read file", e);
        }
    }

    @Override
    @Transactional
    public void saveFile(Long projectId, String path, String content) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));

        String cleanPath = path.startsWith("/") ? path.substring(1) : path;
        String objectKey = projectId + "/" + cleanPath;

        try{
            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
            InputStream inputStream = new ByteArrayInputStream(contentBytes);
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(projectBucket)
                            .object(objectKey)
                            .stream(inputStream, contentBytes.length, -1)
                            .contentType(determineContentType(path))
                            .build());

            ProjectFile file = projectFileRepository.findByProjectIdAndPath(projectId, cleanPath)
                    .orElseGet(() -> ProjectFile.builder()
                            .project(project)
                            .path(cleanPath)
                            .minioObjectKey(objectKey)
                            .createdAt(Instant.now())
                            .build());

            file.setUpdatedAt(Instant.now());
            projectFileRepository.save(file);

            log.info("Saved file: {}", objectKey);
        } catch (Exception e){
            log.error("Failed to save file {}/{}", projectId, cleanPath, e);
            throw new RuntimeException("Failed to save file", e);
        }
    }

    private String determineContentType(String path){
        String type = URLConnection.guessContentTypeFromName(path);
        if(type != null) return type;
        if(path.endsWith(".jsx") || path.endsWith(".ts") || path.endsWith(".tsx")) return "text/javascript";
        if(path.endsWith(".json")) return "application/json";
        if(path.endsWith(".css")) return "text/css";

        return "text/plain";
    }
}
