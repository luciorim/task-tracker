package com.luciorim.task_tracker.api.util;

import com.luciorim.task_tracker.api.Exceptions.NotFoundException;
import com.luciorim.task_tracker.store.entities.ProjectEntity;
import com.luciorim.task_tracker.store.entities.TaskEntity;
import com.luciorim.task_tracker.store.entities.TaskStateEntity;
import com.luciorim.task_tracker.store.repositories.ProjectRepository;
import com.luciorim.task_tracker.store.repositories.TaskRepository;
import com.luciorim.task_tracker.store.repositories.TaskStateRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ControllerHelper {

    ProjectRepository projectRepository;

    TaskStateRepository taskStateRepository;

    TaskRepository taskRepository;

    public ProjectEntity getProjectOrThrowException(Long id) {
        return projectRepository
                .findById(id)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format(
                                        "Project with id \"%s\" does not exist",
                                        id)
                        )
                );
    }

    public TaskStateEntity getTaskStateOrGetException(Long taskStateId){
        return taskStateRepository
                .findById(taskStateId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format(
                                        "Task state with id \"%s\" does not exist",
                                        taskStateId)
                        )
                );
    }

    public TaskEntity getTaskOrGetException(Long taskId){
        return taskRepository
                .findById(taskId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format(
                                        "Task with id \"%s\" does not exist",
                                        taskId)
                        )
                );
    }

}
