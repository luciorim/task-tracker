package com.luciorim.task_tracker.api.controllers;

import com.luciorim.task_tracker.api.Exceptions.BadRequestException;
import com.luciorim.task_tracker.api.Factories.TaskDtoFactory;
import com.luciorim.task_tracker.api.dto.AskDto;
import com.luciorim.task_tracker.api.dto.TaskDto;
import com.luciorim.task_tracker.api.util.ControllerHelper;
import com.luciorim.task_tracker.store.entities.TaskEntity;
import com.luciorim.task_tracker.store.entities.TaskStateEntity;
import com.luciorim.task_tracker.store.repositories.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@RequiredArgsConstructor
public class TaskController {

    TaskRepository taskRepository;

    TaskDtoFactory taskDtoFactory;

    ControllerHelper controllerHelper;

    public static final String CREATE_TASK = "/api/task-states/{task_state_id}";
    public static final String FETCH_TASK = "/api/task-states/{task_state_id}";
    public static final String DELETE_TASK = "/api/tasks/{task_id}";
    public static final String UPDATE_TASK = "/api/tasks/{task_id}";

    //TODO add description to create and update

    @Cacheable(value = "tasks", key = "{#task_state_id, #prefix_name.orElse('')}")
    @GetMapping(FETCH_TASK)
    public List<TaskDto> getTasks(
            @PathVariable(value = "task_state_id") Long task_state_id,
            @RequestParam(value = "prefix_name") Optional<String> prefix_name){

        TaskStateEntity taskState = controllerHelper.getTaskStateOrGetException(task_state_id);

        prefix_name = prefix_name.filter(tempPrefixName -> !tempPrefixName.trim().isEmpty());

        List<TaskEntity> taskEntities = prefix_name
                .map(pref ->
                        taskRepository
                                .streamAllByTaskStateIdAndNameStartsWithIgnoreCase(taskState.getId(), pref)
                                .collect(Collectors.toList())
                )
                .orElseGet(() ->
                        taskRepository
                                .streamAllByTaskStateId(taskState.getId())
                                .collect(Collectors.toList()));

        return taskEntities
                .stream()
                .map(taskDtoFactory::createTaskDto)
                .collect(Collectors.toList());

    }


    @CachePut(value = "tasks", key = "{#task_tate_id, #task_name}")
    @PostMapping(CREATE_TASK)
    public TaskDto createTask(
            @PathVariable(value = "task_state_id") Long task_tate_id,
            @RequestParam(value = "task_name") String task_name){

        if(task_name.trim().isEmpty()){
            throw new BadRequestException("Name of task can't be empty");
        }

        TaskStateEntity taskState = controllerHelper.getTaskStateOrGetException(task_tate_id);

        TaskEntity task = taskRepository.saveAndFlush(
                TaskEntity.builder()
                        .name(task_name)
                        .taskState(taskState)
                        .build()
        );

        final TaskEntity savedTaskEntity = taskRepository.saveAndFlush(task);

        return taskDtoFactory.createTaskDto(savedTaskEntity);
    }

    @CacheEvict(value = "tasks", key = "{#taskId}")
    @DeleteMapping(DELETE_TASK)
    public AskDto deleteTask(@PathVariable(value = "task_id") Long taskId){

        TaskEntity task = controllerHelper.getTaskOrGetException(taskId);

        taskRepository.delete(task);

        return AskDto.builder().answer(true).build();
    }


    @PatchMapping(UPDATE_TASK)
    public TaskDto updateTask(
            @PathVariable(value = "task_id") Long task_id,
            @RequestParam(value = "task_name") String task_name){

        if(task_name.trim().isEmpty()){
            throw new BadRequestException("Task name can't be empty.");
        }

        TaskEntity task = controllerHelper.getTaskOrGetException(task_id);

        taskRepository
                .findTaskEntityByTaskStateIdAndNameContainsIgnoreCase(
                        task.getTaskState().getId(),
                        task_name
                )
                .filter(anotherTask -> !anotherTask.getId().equals(task_id))
                .ifPresent(anotherTask -> {
                    throw new BadRequestException("Task state " + task_name + " already exists.");
                });

        task.setName(task_name);

        task = taskRepository.saveAndFlush(task);

        return taskDtoFactory.createTaskDto(task);
    }

}
