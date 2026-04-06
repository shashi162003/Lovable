package com.devshashi.distributed_lovable.common_lib.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProjectPermission {
    VIEW("project:view"),
    EDIT("project:edit"),
    DELETE("project:delete"),
    VIEW_MEMBERS("project_members:view"),
    MANAGE_MEMBERS("project_members:manage");

    private final String value;
}
