package com.luciorim.task_tracker.api.controllers;

import com.luciorim.task_tracker.api.Exceptions.BadRequestException;
import com.luciorim.task_tracker.api.Factories.TaskStateDtoFactory;
import com.luciorim.task_tracker.api.dto.AskDto;
import com.luciorim.task_tracker.api.dto.TaskStateDto;
import com.luciorim.task_tracker.api.util.ControllerHelper;
import com.luciorim.task_tracker.store.entities.ProjectEntity;
import com.luciorim.task_tracker.store.entities.TaskStateEntity;
import com.luciorim.task_tracker.store.repositories.TaskStateRepository;
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
@RequiredArgsConstructor
@Transactional
public class TaskStateController {

    TaskStateRepository taskStateRepository;

    TaskStateDtoFactory taskStateDtoFactory;

    ControllerHelper controllerHelper;

    public static final String FETCH_TASKS_STATES = "/api/projects/{project_id}/task-states";
    public static final String CREATE_TASK_STATE = "/api/projects/{project_id}/task-states";
    public static final String UPDATE_TASK_STATE = "/api/task-states/{task_state_id}";
    public static final String DELETE_TASK_STATE = "/api/task-states/{task_state_id}";

//    public static final String CHANGE_TASK_STATE_POSITION = ""; // will be added later
    @Cacheable(value = "taskStates", key = "#project_id")
    @GetMapping(FETCH_TASKS_STATES)
    public List<TaskStateDto> getTaskStates(@PathVariable(value = "project_id") Long project_id){

        ProjectEntity projectEntity = controllerHelper.getProjectOrThrowException(project_id);

        return projectEntity
                .getTaskStates()
                .stream()
                .map(taskStateDtoFactory::createTaskStateDto)
                .collect(Collectors.toList());
    }


    @CachePut(value = "taskStates", key = "#project_id")
    @PostMapping(CREATE_TASK_STATE)
    public TaskStateDto createTaskState(
            @PathVariable(value = "project_id") Long project_id,
            @RequestParam(name = "task_state_name") String task_state_name){

        if(task_state_name.trim().isEmpty()){
            throw new BadRequestException("Name of task state can't be empty");
        }

        ProjectEntity project = controllerHelper.getProjectOrThrowException(project_id);

        Optional<TaskStateEntity> optionalTaskStateEntity = Optional.empty();

        for(TaskStateEntity taskStateEntity: project.getTaskStates()){

            if(taskStateEntity.getName().equalsIgnoreCase(task_state_name)){
                throw new BadRequestException("Task state \"" + task_state_name + "\" already exists");
            }

            if(taskStateEntity.getRightTaskState().isEmpty()){
                optionalTaskStateEntity = Optional.of(taskStateEntity);
                break;
            }
        }

        TaskStateEntity taskState = taskStateRepository.saveAndFlush(
                TaskStateEntity.builder()
                        .name(task_state_name)
                        .project(project)
                        .build()
        );

        optionalTaskStateEntity
                .ifPresent(tempTaskState -> {
                    taskState.setLeftTaskState(tempTaskState);
                    tempTaskState.setRightTaskState(taskState);

                    taskStateRepository.saveAndFlush(tempTaskState);
                });

        final TaskStateEntity savedTaskState = taskStateRepository.saveAndFlush(taskState);

        return taskStateDtoFactory.createTaskStateDto(savedTaskState);
    }


    @CachePut(value = "taskStates", key = "#task_state_id")
    @PatchMapping(UPDATE_TASK_STATE)
    public TaskStateDto updateTaskState(
            @PathVariable(value = "task_state_id") Long task_state_id,
            @RequestParam(value = "task_state_name") String new_task_state_name){

        if(new_task_state_name.trim().isEmpty()){
            throw new BadRequestException("Task state name can not be empty.");
        }

        TaskStateEntity taskState = controllerHelper.getTaskStateOrGetException(task_state_id);

        taskStateRepository
                .findTaskStateEntityByProjectIdAndNameContainsIgnoreCase(
                        taskState.getProject().getId(),
                        new_task_state_name
                )
                .filter(anotherTaskState -> !anotherTaskState.getId().equals(task_state_id))
                .ifPresent(anotherTaskState -> {
                    throw new BadRequestException("Task state " + new_task_state_name + " already exists.");
                });

        taskState.setName(new_task_state_name);

        taskState = taskStateRepository.saveAndFlush(taskState);

        return taskStateDtoFactory.createTaskStateDto(taskState);

    }


    @DeleteMapping(DELETE_TASK_STATE)
    public AskDto deleteTaskState(@PathVariable(value = "task_state_id") Long taskStateId){

        TaskStateEntity taskState = controllerHelper.getTaskStateOrGetException(taskStateId);

        Optional<TaskStateEntity> optionalLeftTaskState = taskState.getLeftTaskState();
        Optional<TaskStateEntity> optionalRightTaskState = taskState.getRightTaskState();

        optionalLeftTaskState
                .ifPresent(tempTaskState -> {

                    tempTaskState.setRightTaskState(optionalRightTaskState.orElse(null));

                    taskStateRepository.saveAndFlush(tempTaskState);

                });

        optionalRightTaskState
                .ifPresent(tempTaskState -> {

                    tempTaskState.setLeftTaskState(optionalLeftTaskState.orElse(null));

                    taskStateRepository.saveAndFlush(tempTaskState);

                });

        taskState = taskStateRepository.saveAndFlush(taskState);

        taskStateRepository.delete(taskState);

        return AskDto.builder().answer(true).build();



    }



}
