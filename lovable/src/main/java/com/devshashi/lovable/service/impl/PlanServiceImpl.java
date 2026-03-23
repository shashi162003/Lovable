package com.devshashi.lovable.service.impl;

import com.devshashi.lovable.dto.subscription.PlanResponse;
import com.devshashi.lovable.service.PlanService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanServiceImpl implements PlanService {

    @Override
    public List<PlanResponse> getAllActivePlans() {
        return List.of();
    }
}
