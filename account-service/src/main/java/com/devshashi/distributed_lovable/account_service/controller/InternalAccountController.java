package com.devshashi.distributed_lovable.account_service.controller;

import com.devshashi.distributed_lovable.account_service.mapper.UserMapper;
import com.devshashi.distributed_lovable.account_service.repository.UserRepository;
import com.devshashi.distributed_lovable.account_service.service.SubscriptionService;
import com.devshashi.distributed_lovable.common_lib.dto.PlanDTO;
import com.devshashi.distributed_lovable.common_lib.dto.UserDTO;
import com.devshashi.distributed_lovable.common_lib.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/internal/v1")
@RequiredArgsConstructor
public class InternalAccountController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SubscriptionService subscriptionService;

    @GetMapping("/users/{id}")
    public UserDTO getUserById(@PathVariable Long id){
        return userRepository.findById(id)
                .map(userMapper::toUserDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User", id.toString()));
    }

    @GetMapping("/users/by-email")
    public Optional<UserDTO> getUserByEmail(@RequestParam String email){
        return userRepository.findByUsernameIgnoreCase(email)
                .map(userMapper::toUserDTO);
    }

    @GetMapping("/billing/current-plan")
    public PlanDTO getCurrentSubscribedPlan(){
        return subscriptionService.getCurrentSubscribedPlanByUser();
    }

}
