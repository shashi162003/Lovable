package com.devshashi.lovable.service.impl;

import com.devshashi.lovable.dto.subscription.PlanLimitsResponse;
import com.devshashi.lovable.dto.subscription.UsageTodayResponse;
import com.devshashi.lovable.service.UsageService;
import org.springframework.stereotype.Service;

@Service
public class UsageServiceImpl implements UsageService {

    @Override
    public UsageTodayResponse getTodayUsageOfUser(Long userId) {
        return null;
    }

    @Override
    public PlanLimitsResponse getCurrentSubscriptionLimitsOfUser(Long userId) {
        return null;
    }
}
