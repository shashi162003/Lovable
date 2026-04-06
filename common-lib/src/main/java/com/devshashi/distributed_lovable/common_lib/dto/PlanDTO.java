package com.devshashi.distributed_lovable.common_lib.dto;

public record PlanDTO(
        Long id,
        String name,
        Integer maxProjects,
        Integer maxTokensPerDay,
        Boolean unlimitedAi,
        String price
) {
}
