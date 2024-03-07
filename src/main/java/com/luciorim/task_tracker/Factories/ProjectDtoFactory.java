package com.luciorim.task_tracker.Factories;

import com.luciorim.task_tracker.dto.ProjectDto;
import com.luciorim.task_tracker.entities.ProjectEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectDtoFactory {

    public ProjectDto createProjectDto(ProjectEntity projectEntity){
        return ProjectDto.builder()
                .id(projectEntity.getId())
                .name(projectEntity.getName())
                .lastUpdate(projectEntity.getLastUpdate())
                .creationDate(projectEntity.getCreationDate())
                .build();
    }

}
