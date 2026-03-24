package com.devshashi.lovable.mapper;

import com.devshashi.lovable.dto.auth.SignupRequest;
import com.devshashi.lovable.dto.auth.UserProfileResponse;
import com.devshashi.lovable.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(SignupRequest signupRequest);

    UserProfileResponse toUserProfileResponse(User user);

}
