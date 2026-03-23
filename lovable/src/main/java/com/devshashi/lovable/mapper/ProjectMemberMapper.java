package com.devshashi.lovable.mapper;

import com.devshashi.lovable.dto.member.MemberResponse;
import com.devshashi.lovable.entity.ProjectMember;
import com.devshashi.lovable.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMemberMapper {

    @Mapping(source = "id", target = "userId")
    @Mapping(target = "projectRole", constant = "OWNER")
    MemberResponse toProjectMemberResponseFromOwner(User owner);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.name", target = "name")
    MemberResponse toProjectMemberResponseFromMember(ProjectMember member);
}