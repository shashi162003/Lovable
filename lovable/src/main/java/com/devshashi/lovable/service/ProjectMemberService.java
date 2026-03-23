package com.devshashi.lovable.service;

import com.devshashi.lovable.dto.member.InviteMemberRequest;
import com.devshashi.lovable.dto.member.MemberResponse;
import com.devshashi.lovable.dto.member.UpdateMemberRoleRequest;
import com.devshashi.lovable.entity.ProjectMember;

import java.util.List;

public interface ProjectMemberService {
    List<ProjectMember> getProjectMembers(Long projectId, Long userId);

    MemberResponse inviteMember(Long projectId, InviteMemberRequest request, Long userId);

    MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest request, Long userId);

    MemberResponse deleteProjectMember(Long projectId, Long memberId, Long userId);
}
