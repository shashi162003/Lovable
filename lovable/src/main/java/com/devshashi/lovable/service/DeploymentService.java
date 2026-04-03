package com.devshashi.lovable.service;

import com.devshashi.lovable.dto.deploy.DeployResponse;

public interface DeploymentService {

    DeployResponse deploy(Long projectId);

}
