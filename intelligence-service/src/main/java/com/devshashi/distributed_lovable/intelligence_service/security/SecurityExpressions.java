package com.devshashi.distributed_lovable.intelligence_service.security;

import com.devshashi.distributed_lovable.common_lib.enums.ProjectPermission;
import com.devshashi.distributed_lovable.common_lib.security.AuthUtil;
import com.devshashi.distributed_lovable.intelligence_service.client.WorkspaceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component("security")
@RequiredArgsConstructor
@Slf4j
public class SecurityExpressions {

    private final AuthUtil authUtil;
    private final WorkspaceClient workspaceClient;

    private Boolean hasPermission(Long projectId, ProjectPermission projectPermission){
        return workspaceClient.checkPermission(projectId, projectPermission);
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
