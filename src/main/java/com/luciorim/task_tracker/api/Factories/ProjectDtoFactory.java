package com.luciorim.task_tracker.api.Factories;

import com.luciorim.task_tracker.api.dto.ProjectDto;
import com.luciorim.task_tracker.store.entities.ProjectEntity;
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
