package com.devshashi.distributed_lovable.workspace_service.mapper;

import com.devshashi.distributed_lovable.workspace_service.dto.project.FileNode;
import com.devshashi.distributed_lovable.workspace_service.entity.ProjectFile;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectFileMapper {

    List<FileNode> toListOfFileNode(List<ProjectFile> projectFileList);
}
