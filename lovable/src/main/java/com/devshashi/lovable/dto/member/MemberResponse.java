package com.devshashi.lovable.dto.member;

import com.devshashi.lovable.enums.ProjectRole;

import java.time.Instant;

public record MemberResponse(
    Long userId,
    String username,
    String name,
    ProjectRole projectRole,
    Instant invitedAt
) {
}
