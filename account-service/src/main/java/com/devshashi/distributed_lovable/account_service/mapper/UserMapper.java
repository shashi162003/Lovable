package com.devshashi.distributed_lovable.account_service.mapper;

import com.devshashi.distributed_lovable.account_service.dto.auth.SignupRequest;
import com.devshashi.distributed_lovable.account_service.dto.auth.UserProfileResponse;
import com.devshashi.distributed_lovable.account_service.entity.User;
import com.devshashi.distributed_lovable.common_lib.dto.UserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(SignupRequest signupRequest);

    UserProfileResponse toUserProfileResponse(User user);

    UserDTO toUserDTO(User user);

}
