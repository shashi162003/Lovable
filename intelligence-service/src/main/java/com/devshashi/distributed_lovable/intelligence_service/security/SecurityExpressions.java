package com.devshashi.distributed_lovable.intelligence_service.security;

import com.devshashi.distributed_lovable.common_lib.enums.ProjectPermission;
import com.devshashi.distributed_lovable.common_lib.security.AuthUtil;
import com.devshashi.distributed_lovable.intelligence_service.client.WorkspaceClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.stereotype.Component;

@Component("security")
@RequiredArgsConstructor
@Slf4j
public class SecurityExpressions {

    private final AuthUtil authUtil;
    private final WorkspaceClient workspaceClient;

    private Boolean hasPermission(Long projectId, ProjectPermission projectPermission){
        try{
            return workspaceClient.checkPermission(projectId, projectPermission);
        } catch (FeignException.Unauthorized e){
            log.warn("Token expired or invalid during permission check for project: {}", projectId);
            throw new CredentialsExpiredException("JWT token is expired or invalid");
        } catch (FeignException e){
            log.error("Workspace-service failed during permission check: {}", e.getMessage());
            return false;
        }
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
