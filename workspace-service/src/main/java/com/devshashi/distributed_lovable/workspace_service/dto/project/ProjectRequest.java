package com.devshashi.distributed_lovable.workspace_service.dto.project;

import jakarta.validation.constraints.NotBlank;

public record ProjectRequest(
        @NotBlank
        String name
) {
}
