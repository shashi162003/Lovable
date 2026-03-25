package com.devshashi.lovable.mapper;

import com.devshashi.lovable.dto.subscription.PlanResponse;
import com.devshashi.lovable.dto.subscription.SubscriptionResponse;
import com.devshashi.lovable.entity.Plan;
import com.devshashi.lovable.entity.Subscription;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    SubscriptionResponse toSubscriptionResponse(Subscription subscription);

    PlanResponse toPlanResponse(Plan plan);

}
