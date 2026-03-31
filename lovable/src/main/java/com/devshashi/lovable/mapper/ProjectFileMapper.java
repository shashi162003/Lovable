package com.devshashi.lovable.mapper;

import com.devshashi.lovable.dto.project.FileNode;
import com.devshashi.lovable.entity.ProjectFile;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectFileMapper {

    List<FileNode> toListOfFileNode(List<ProjectFile> projectFileList);
}
