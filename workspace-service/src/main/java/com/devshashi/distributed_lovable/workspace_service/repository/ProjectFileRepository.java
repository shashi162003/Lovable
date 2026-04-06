package com.devshashi.distributed_lovable.workspace_service.repository;

import com.devshashi.distributed_lovable.workspace_service.entity.ProjectFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectFileRepository extends JpaRepository<ProjectFile, Long> {
    Optional<ProjectFile> findByProjectIdAndPath(Long projectId, String cleanPath);

    List<ProjectFile> findByProjectId(Long projectId);
}