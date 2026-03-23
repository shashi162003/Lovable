package com.devshashi.lovable.dto.member;

import com.devshashi.lovable.enums.ProjectRole;

import java.time.Instant;

public record MemberResponse(
    Long userId,
    String email,
    String name,
    String avatarUrl,
    ProjectRole role,
    Instant invitedAt
) {
}
