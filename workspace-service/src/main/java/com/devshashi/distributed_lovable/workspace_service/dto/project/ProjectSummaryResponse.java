package com.devshashi.distributed_lovable.workspace_service.dto.project;

import com.devshashi.distributed_lovable.common_lib.enums.ProjectRole;

import java.time.Instant;

public record ProjectSummaryResponse(
        Long id,
        String name,
        Instant createdAt,
        Instant updatedAt,
        ProjectRole role
) {
}
