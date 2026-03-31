package com.devshashi.lovable.security;

import com.devshashi.lovable.enums.ProjectPermission;
import com.devshashi.lovable.repository.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component("security")
@RequiredArgsConstructor
@Slf4j
public class SecurityExpressions {

    private final ProjectMemberRepository projectMemberRepository;
    private final AuthUtil authUtil;

    private Boolean hasPermission(Long projectId, ProjectPermission projectPermission){
        Long userId = authUtil.getCurrentUserId();
        return projectMemberRepository.findRoleByProjectIdAndUserId(projectId, userId)
                .map(role -> role.getPermissions().contains(projectPermission))
                .orElse(false);
    }

    public Boolean canViewProject(Long projectId){
        return hasPermission(projectId, ProjectPermission.VIEW);
    }

    public Boolean canEditProject(Long projectId){
        Long userId = authUtil.getCurrentUserId();
        log.debug("canEditProject called - projectId: {}, userId: {}", projectId, userId);

        if (userId == null) {
            log.error("userId is null - SecurityContext is empty!");
            return false;
        }
        return hasPermission(projectId, ProjectPermission.EDIT);
    }

    public Boolean canDeleteProject(Long projectId){
        return hasPermission(projectId, ProjectPermission.DELETE);
    }

    public Boolean canViewMembers(Long projectId){
        return hasPermission(projectId, ProjectPermission.VIEW_MEMBERS);
    }

    public Boolean canManageMembers(Long projectId){
        return hasPermission(projectId, ProjectPermission.MANAGE_MEMBERS);
    }

}
