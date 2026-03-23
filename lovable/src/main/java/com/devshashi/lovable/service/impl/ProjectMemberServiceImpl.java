package com.devshashi.lovable.service.impl;

import com.devshashi.lovable.dto.member.InviteMemberRequest;
import com.devshashi.lovable.dto.member.MemberResponse;
import com.devshashi.lovable.dto.member.UpdateMemberRoleRequest;
import com.devshashi.lovable.entity.Project;
import com.devshashi.lovable.entity.ProjectMember;
import com.devshashi.lovable.entity.ProjectMemberId;
import com.devshashi.lovable.entity.User;
import com.devshashi.lovable.mapper.ProjectMemberMapper;
import com.devshashi.lovable.repository.ProjectMemberRepository;
import com.devshashi.lovable.repository.ProjectRepository;
import com.devshashi.lovable.repository.UserRepository;
import com.devshashi.lovable.service.ProjectMemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectMemberServiceImpl implements ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberMapper projectMemberMapper;
    private final UserRepository userRepository;

    @Override
    public List<MemberResponse> getProjectMembers(Long projectId, Long userId) {
        Project project = getAccessibleProjectById(projectId, userId);
        List<MemberResponse> memberResponseList = new ArrayList<>();
        memberResponseList.add(projectMemberMapper.toProjectMemberResponseFromOwner(project.getOwner()));
        memberResponseList.addAll(
            projectMemberRepository.findByIdProjectId(projectId)
                    .stream()
                    .map(projectMemberMapper::toProjectMemberResponseFromMember)
                    .toList()
        );
        return memberResponseList;
    }

    @Override
    public MemberResponse inviteMember(Long projectId, InviteMemberRequest request, Long userId) {
        Project project = getAccessibleProjectById(projectId, userId);
        if(!project.getOwner().getId().equals(userId)){
            throw new RuntimeException("Not allowed to invite member");
        }
        User invitee = userRepository.findByEmail(request.email()).orElseThrow();
        if(invitee.getId().equals(userId)){
            throw new RuntimeException("Cannot invite yourself");
        }
        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, invitee.getId());
        if(projectMemberRepository.existsById(projectMemberId)){
            throw new RuntimeException("Cannot invite once again");
        }
        ProjectMember member = ProjectMember.builder()
                .id(projectMemberId)
                .project(project)
                .user(invitee)
                .projectRole(request.role())
                .invitedAt(Instant.now())
                .build();
        projectMemberRepository.save(member);
        return projectMemberMapper.toProjectMemberResponseFromMember(member);
    }

    @Override
    public MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest request, Long userId) {
        Project project = getAccessibleProjectById(projectId, userId);
        if(!project.getOwner().getId().equals(userId)){
            throw new RuntimeException("Not allowed to update member role");
        }
        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);
        ProjectMember projectMember = projectMemberRepository.findById(projectMemberId).orElseThrow();
        projectMember.setProjectRole(request.role());
        projectMemberRepository.save(projectMember);
        return projectMemberMapper.toProjectMemberResponseFromMember(projectMember);
    }

    @Override
    public void removeProjectMember(Long projectId, Long memberId, Long userId) {
        Project project = getAccessibleProjectById(projectId, userId);
        if(!project.getOwner().getId().equals(userId)){
            throw new RuntimeException("Not allowed to remove member from project");
        }
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
