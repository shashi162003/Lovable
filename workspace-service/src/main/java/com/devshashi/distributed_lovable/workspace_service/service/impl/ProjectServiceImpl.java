package com.devshashi.distributed_lovable.workspace_service.service.impl;

import com.devshashi.distributed_lovable.common_lib.dto.PlanDTO;
import com.devshashi.distributed_lovable.common_lib.enums.ProjectPermission;
import com.devshashi.distributed_lovable.common_lib.enums.ProjectRole;
import com.devshashi.distributed_lovable.common_lib.error.BadRequestException;
import com.devshashi.distributed_lovable.common_lib.error.ResourceNotFoundException;
import com.devshashi.distributed_lovable.common_lib.security.AuthUtil;
import com.devshashi.distributed_lovable.workspace_service.client.AccountClient;
import com.devshashi.distributed_lovable.workspace_service.dto.project.ProjectRequest;
import com.devshashi.distributed_lovable.workspace_service.dto.project.ProjectResponse;
import com.devshashi.distributed_lovable.workspace_service.dto.project.ProjectSummaryResponse;
import com.devshashi.distributed_lovable.workspace_service.entity.Project;
import com.devshashi.distributed_lovable.workspace_service.entity.ProjectMember;
import com.devshashi.distributed_lovable.workspace_service.entity.ProjectMemberId;
import com.devshashi.distributed_lovable.workspace_service.mapper.ProjectMapper;
import com.devshashi.distributed_lovable.workspace_service.repository.ProjectMemberRepository;
import com.devshashi.distributed_lovable.workspace_service.repository.ProjectRepository;
import com.devshashi.distributed_lovable.workspace_service.security.SecurityExpressions;
import com.devshashi.distributed_lovable.workspace_service.service.ProjectService;
import com.devshashi.distributed_lovable.workspace_service.service.ProjectTemplateService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ProjectMemberRepository projectMemberRepository;
    private final AuthUtil authUtil;
    private final ProjectTemplateService projectTemplateService;
    private final AccountClient accountClient;
    private final SecurityExpressions securityExpressions;

    @Override
    public ProjectResponse createProject(ProjectRequest request) {

        if(!canCreateProject()){
            throw new BadRequestException("User cannot create new project with current Plan, upgrade Plan now");
        }

        Long ownerUserId = authUtil.getCurrentUserId();

        Project project = Project.builder()
                .name(request.name())
                .isPublic(false)
                .build();
        project = projectRepository.save(project);
        ProjectMemberId projectMemberId = new ProjectMemberId(project.getId(), ownerUserId);
        ProjectMember projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .projectRole(ProjectRole.OWNER)
                .acceptedAt(Instant.now())
                .invitedAt(Instant.now())
                .project(project)
                .build();
        projectMemberRepository.save(projectMember);
        projectTemplateService.initializeProjectFromTemplate(project.getId());
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public List<ProjectSummaryResponse> getUserProjects() {
        Long userId = authUtil.getCurrentUserId();
        var projectsWithRole = projectRepository.findAllAccessibleByUser(userId);
        return projectsWithRole.stream()
                .map(p -> projectMapper.toProjectSummaryResponse(p.getProject(), p.getRole()))
                .toList();
    }

    @Override
    @PreAuthorize("@security.canViewProject(#projectId)")
    public ProjectSummaryResponse getUserProjectById(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        var projectWithRole = projectRepository.findAccessibleProjectByIdWithRole(projectId, userId)
                .orElseThrow(() -> new BadRequestException("Project not found"));
        Project project = projectWithRole.getProject();
        ProjectRole role = projectWithRole.getRole();
        return projectMapper.toProjectSummaryResponse(project, role);
    }

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public ProjectResponse updateProject(Long projectId, ProjectRequest request) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId, userId);
        project.setName(request.name());
        project = projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    @PreAuthorize("@security.canDeleteProject(#projectId)")
    public void softDelete(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId, userId);
        project.setDeletedAt(Instant.now());
        projectRepository.save(project);
    }

    @Override
    public boolean hasPermission(Long projectId, ProjectPermission permission) {
        return securityExpressions.hasPermission(projectId, permission);
    }

    /// INTERNAL FUNCTIONS

    private Project getAccessibleProjectById(Long projectId, Long userId){
        return projectRepository.findAccessibleProjectById(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));
    }

    private boolean canCreateProject(){
        Long userId = authUtil.getCurrentUserId();
        if(userId == null) return false;
        PlanDTO plan = accountClient.getCurrentSubscribedPlanByUser();
        int maxAllowed = plan.maxProjects();
        int ownedCount = projectMemberRepository.countProjectOwnedByUser(userId);
        return ownedCount < maxAllowed;
    }
}
