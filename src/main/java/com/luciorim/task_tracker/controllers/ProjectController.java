package com.luciorim.task_tracker.controllers;

import com.luciorim.task_tracker.Exceptions.BadRequestException;
import com.luciorim.task_tracker.Factories.ProjectDtoFactory;
import com.luciorim.task_tracker.dto.AskDto;
import com.luciorim.task_tracker.dto.ProjectDto;
import com.luciorim.task_tracker.util.ControllerHelper;
import com.luciorim.task_tracker.entities.ProjectEntity;
import com.luciorim.task_tracker.repositories.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@RequiredArgsConstructor
public class ProjectController {

    ProjectRepository projectRepository;

    ProjectDtoFactory projectDtoFactory;

    ControllerHelper controllerHelper;

    public static final String CREATE_OR_UPDATE_PROJECT = "/api/projects";
    public static final String FETCH_PROJECT = "/api/projects";
    public static final String DELETE_PROJECT = "/apo/projects/{project_id}";

    @Cacheable(value = "projects", key = "{#prefixName.orElse('')}")
    @GetMapping(FETCH_PROJECT)
    public List<ProjectDto> fetchProjects(
            @RequestParam(value = "prefix_name", required = false) Optional<String> prefixName) {

        //will be null if there is no request param
        prefixName = prefixName.filter(tempPrefixName -> !tempPrefixName.trim().isEmpty());

        Stream<ProjectEntity> projectEntityStream = prefixName
                .map(projectRepository::streamAllByNameStartsWithIgnoreCase)
                .orElseGet(projectRepository::streamAllBy);

        return projectEntityStream
                .map(projectDtoFactory::createProjectDto)
                .collect(Collectors.toList());

    }

    @CachePut(value = "projects", key = "{#project_id.orElse(''), #project_name.orElse('')}")
    @PostMapping(CREATE_OR_UPDATE_PROJECT)
    public ProjectDto createOrUpdaterProject(
            @RequestParam(value = "project_id", required = false) Optional<Long> project_id,
            @RequestParam(value = "project_name", required = false) Optional<String> project_name){

        project_name = project_name.filter(anotherName -> !anotherName.trim().isEmpty());

        boolean needCreate = project_id.isEmpty();

        ProjectEntity projectEntity = project_id
                .map(controllerHelper::getProjectOrThrowException)
                .orElseGet(() -> ProjectEntity.builder().build());

        if(needCreate && project_name.isEmpty()){
            throw new BadRequestException("Project name can't be empty");
        }

        project_name
                .ifPresent(anotherName -> {
                    projectRepository
                            .findByName(anotherName)
                            .filter(tempProj -> !Objects.equals(tempProj.getId(), projectEntity.getId()))
                            .ifPresent(anotherProj -> {
                                throw new BadRequestException(String.format("Project with name \"%s\" already exists", anotherName));
                            });

                    projectEntity.setName(anotherName);
                });

        final ProjectEntity toSaveProject = projectRepository.saveAndFlush(projectEntity);

        return projectDtoFactory.createProjectDto(toSaveProject);


    }


    @DeleteMapping(DELETE_PROJECT)
    public AskDto deleteProject(@PathVariable Long project_id) {
        ProjectEntity project = controllerHelper.getProjectOrThrowException(project_id);

        projectRepository.delete(project);

        return AskDto.createAnswer(true);
    }



}
