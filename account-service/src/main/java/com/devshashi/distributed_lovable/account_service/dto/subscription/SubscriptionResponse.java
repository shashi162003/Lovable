package com.devshashi.distributed_lovable.account_service.dto.subscription;

import com.devshashi.distributed_lovable.common_lib.dto.PlanDTO;

import java.time.Instant;

public record SubscriptionResponse(
        PlanDTO plan,
        String status,
        Instant currentPeriodEnd,
        Long tokensUsedThisCycle
) {
}
