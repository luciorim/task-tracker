package com.luciorim.task_tracker.Factories;

import com.luciorim.task_tracker.dto.TaskDto;
import com.luciorim.task_tracker.entities.TaskEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskDtoFactory {

    public TaskDto createTaskDto(TaskEntity taskEntity){
        return TaskDto.builder()
                .id(taskEntity.getId())
                .name(taskEntity.getName())
                .creationDate(taskEntity.getCreationDate())
                .description(taskEntity.getDescription())
                .build();
    }

}
