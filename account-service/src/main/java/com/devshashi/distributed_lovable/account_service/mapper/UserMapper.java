package com.devshashi.distributed_lovable.account_service.mapper;

import com.devshashi.distributed_lovable.account_service.dto.auth.SignupRequest;
import com.devshashi.distributed_lovable.account_service.dto.auth.UserProfileResponse;
import com.devshashi.distributed_lovable.account_service.entity.User;
import com.devshashi.distributed_lovable.common_lib.dto.UserDTO;
import com.devshashi.distributed_lovable.common_lib.security.JwtUserPrincipal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(SignupRequest signupRequest);

    @Mapping(source = "userId", target = "id")
    UserProfileResponse toUserProfileResponse(JwtUserPrincipal user);

    UserDTO toUserDTO(User user);

}
