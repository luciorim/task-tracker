package com.luciorim.task_tracker.api.Factories;

import com.luciorim.task_tracker.api.dto.TaskStateDto;
import com.luciorim.task_tracker.store.entities.TaskStateEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskStateDtoFactory {

    public TaskStateDto createTaskStateDto(TaskStateEntity taskStateEntity){
        return TaskStateDto.builder()
                .id(taskStateEntity.getId())
                .name(taskStateEntity.getName())
                .creationDate(taskStateEntity.getCreationDate())
                .ordinal(taskStateEntity.getOrdinal())
                .build();
    }

}
