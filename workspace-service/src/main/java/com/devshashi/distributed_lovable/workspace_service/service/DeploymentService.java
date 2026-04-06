package com.devshashi.distributed_lovable.workspace_service.service;

import com.devshashi.distributed_lovable.workspace_service.dto.deploy.DeployResponse;

public interface DeploymentService {

    DeployResponse deploy(Long projectId);

}
