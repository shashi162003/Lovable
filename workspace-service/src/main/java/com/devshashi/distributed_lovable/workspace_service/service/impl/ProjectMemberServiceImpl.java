package com.devshashi.distributed_lovable.workspace_service.service.impl;

import com.devshashi.distributed_lovable.common_lib.dto.UserDTO;
import com.devshashi.distributed_lovable.common_lib.error.ResourceNotFoundException;
import com.devshashi.distributed_lovable.common_lib.security.AuthUtil;
import com.devshashi.distributed_lovable.workspace_service.client.AccountClient;
import com.devshashi.distributed_lovable.workspace_service.dto.member.InviteMemberRequest;
import com.devshashi.distributed_lovable.workspace_service.dto.member.MemberResponse;
import com.devshashi.distributed_lovable.workspace_service.dto.member.UpdateMemberRoleRequest;
import com.devshashi.distributed_lovable.workspace_service.entity.Project;
import com.devshashi.distributed_lovable.workspace_service.entity.ProjectMember;
import com.devshashi.distributed_lovable.workspace_service.entity.ProjectMemberId;
import com.devshashi.distributed_lovable.workspace_service.mapper.ProjectMemberMapper;
import com.devshashi.distributed_lovable.workspace_service.repository.ProjectMemberRepository;
import com.devshashi.distributed_lovable.workspace_service.repository.ProjectRepository;
import com.devshashi.distributed_lovable.workspace_service.service.ProjectMemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectMemberServiceImpl implements ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberMapper projectMemberMapper;
    private final AuthUtil authUtil;
    private final AccountClient accountClient;

    @Override
    @PreAuthorize("@security.canViewMembers(#projectId)")
    public List<MemberResponse> getProjectMembers(Long projectId) {
        return projectMemberRepository.findByIdProjectId(projectId)
                .stream()
                .map(projectMemberMapper::toProjectMemberResponseFromMember)
                .toList();
    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public MemberResponse inviteMember(Long projectId, InviteMemberRequest request) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId, userId);
        UserDTO invitee = accountClient.getUserByEmail(request.username())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.username()));
        if(invitee.id().equals(userId)){
            throw new RuntimeException("Cannot invite yourself");
        }
        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, invitee.id());
        if(projectMemberRepository.existsById(projectMemberId)){
            throw new RuntimeException("Cannot invite once again");
        }
        ProjectMember member = ProjectMember.builder()
                .id(projectMemberId)
                .project(project)
                .projectRole(request.role())
                .invitedAt(Instant.now())
                .build();
        projectMemberRepository.save(member);
        return projectMemberMapper.toProjectMemberResponseFromMember(member);
    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest request) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId, userId);
        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);
        ProjectMember projectMember = projectMemberRepository.findById(projectMemberId).orElseThrow();
        projectMember.setProjectRole(request.role());
        projectMemberRepository.save(projectMember);
        return projectMemberMapper.toProjectMemberResponseFromMember(projectMember);
    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public void removeProjectMember(Long projectId, Long memberId) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId, userId);
        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);
        if(!projectMemberRepository.existsById(projectMemberId)){
            throw new RuntimeException("Project member not found");
        }
        projectMemberRepository.deleteById(projectMemberId);
    }

    /// INTERNAL FUNCTIONS

    private Project getAccessibleProjectById(Long projectId, Long userId){
        return projectRepository.findAccessibleProjectById(projectId, userId).orElseThrow();
    }
}
