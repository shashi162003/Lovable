package com.devshashi.distributed_lovable.workspace_service.client;

import com.devshashi.distributed_lovable.common_lib.dto.PlanDTO;
import com.devshashi.distributed_lovable.common_lib.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@FeignClient(name = "account-service", path = "/account", url = "${ACCOUNT_SERVICE_URI:}")
public interface AccountClient {

    @GetMapping("/internal/v1/users/by-email")
    Optional<UserDTO> getUserByEmail(@RequestParam("email") String email);

    @GetMapping("/internal/v1/billing/current-plan")
    PlanDTO getCurrentSubscribedPlanByUser();

}
