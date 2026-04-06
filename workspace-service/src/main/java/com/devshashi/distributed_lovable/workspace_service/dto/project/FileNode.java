package com.devshashi.distributed_lovable.workspace_service.dto.project;

import org.jspecify.annotations.NonNull;

public record FileNode(
        String path
) {
    @Override
    public @NonNull String toString() {
        return path;
    }
}
