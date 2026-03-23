package com.devshashi.lovable.mapper;

import com.devshashi.lovable.dto.project.ProjectResponse;
import com.devshashi.lovable.dto.project.ProjectSummaryResponse;
import com.devshashi.lovable.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectResponse toProjectResponse(Project project);

    @Mapping(source = "name", target = "projectName")
    ProjectSummaryResponse toProjectSummaryResponse(Project project);

    List<ProjectSummaryResponse> toListOfProjectSummaryResponse(List<Project> projects);

}
