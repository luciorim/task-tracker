package com.luciorim.task_tracker.api.Factories;

import com.luciorim.task_tracker.api.dto.TaskStateDto;
import com.luciorim.task_tracker.store.entities.TaskStateEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TaskStateDtoFactory {

    private final TaskDtoFactory taskDtoFactory;

    public TaskStateDto createTaskStateDto(TaskStateEntity taskStateEntity){
        return TaskStateDto.builder()
                .id(taskStateEntity.getId())
                .name(taskStateEntity.getName())
                .creationDate(taskStateEntity.getCreationDate())
                .leftTaskStateId(taskStateEntity.getLeftTaskState().map(TaskStateEntity::getId).orElse(null))
                .rightTaskStateId(taskStateEntity.getRightTaskState().map(TaskStateEntity::getId).orElse(null))
                .tasks(
                        taskStateEntity
                                .getTasks()
                                .stream()
                                .map(taskDtoFactory::createTaskDto)
                                .collect(Collectors.toList())
                )
                .build();
    }

}
