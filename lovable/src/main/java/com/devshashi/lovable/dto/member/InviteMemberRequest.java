package com.devshashi.lovable.dto.member;

import com.devshashi.lovable.enums.ProjectRole;

public record InviteMemberRequest(
        String email,
        ProjectRole role
) {
}
