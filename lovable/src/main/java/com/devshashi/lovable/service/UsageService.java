package com.devshashi.lovable.service;

import com.devshashi.lovable.dto.subscription.PlanLimitsResponse;
import com.devshashi.lovable.dto.subscription.UsageTodayResponse;

public interface UsageService {
    UsageTodayResponse getTodayUsageOfUser(Long userId);

    PlanLimitsResponse getCurrentSubscriptionLimitsOfUser(Long userId);
}
