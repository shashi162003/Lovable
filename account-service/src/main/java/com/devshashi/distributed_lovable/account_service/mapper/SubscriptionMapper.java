package com.devshashi.distributed_lovable.account_service.mapper;

import com.devshashi.distributed_lovable.account_service.dto.subscription.SubscriptionResponse;
import com.devshashi.distributed_lovable.account_service.entity.Plan;
import com.devshashi.distributed_lovable.account_service.entity.Subscription;
import com.devshashi.distributed_lovable.common_lib.dto.PlanDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    SubscriptionResponse toSubscriptionResponse(Subscription subscription);

    PlanDTO toPlanResponse(Plan plan);

}
