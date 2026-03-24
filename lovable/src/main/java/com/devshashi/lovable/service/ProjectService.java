package com.devshashi.lovable.service;

import com.devshashi.lovable.dto.project.ProjectRequest;
import com.devshashi.lovable.dto.project.ProjectResponse;
import com.devshashi.lovable.dto.project.ProjectSummaryResponse;

import java.util.List;

public interface ProjectService {
    List<ProjectSummaryResponse> getUserProjects();

    ProjectResponse getUserProjectById(Long id);

    ProjectResponse createProject(ProjectRequest request);

    ProjectResponse updateProject(Long id, ProjectRequest request);

    void softDelete(Long id);
}
