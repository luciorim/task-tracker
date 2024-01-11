package com.luciorim.task_tracker.api.controllers;

import com.luciorim.task_tracker.api.Exceptions.BadRequestException;
import com.luciorim.task_tracker.api.Exceptions.NotFoundException;
import com.luciorim.task_tracker.api.Factories.ProjectDtoFactory;
import com.luciorim.task_tracker.api.dto.AskDto;
import com.luciorim.task_tracker.api.dto.ProjectDto;
import com.luciorim.task_tracker.store.entities.ProjectEntity;
import com.luciorim.task_tracker.store.repositories.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) //here we make all fields final and private
@Transactional
@RequiredArgsConstructor // automatically creates constructor for final fields
public class ProjectContoller {

    ProjectDtoFactory projectDtoFactory;
    ProjectRepository projectRepository;

    public static final String FETCH_PROJECT = "/api/projects/";
    public static final String CREATE_PROJECT = "/api/projects";
    public static final String EDIT_PROJECT = "/api/projects/{project_id}";
    public static final String DELETE_PROJECT = "/apo/projects/{project_id}";

    @GetMapping(FETCH_PROJECT)
    public List<ProjectDto> fetchProjects(
            @RequestParam(value = "prefix_name", required = false) Optional<String> prefixName) {

        //will be null if there is no request param
        prefixName = prefixName.filter(tempPrefixName -> !tempPrefixName.trim().isEmpty());

        Stream<ProjectEntity> projectEntityStream = prefixName
                .map(projectRepository::streamAllByNameStartsWithIgnoreCase)
                .orElseGet(() -> projectRepository.streamAll());

        return projectEntityStream
                .map(projectDtoFactory::createProjectDto)
                .collect(Collectors.toList());

    }

    @PostMapping(CREATE_PROJECT)
    public ProjectDto createProject(@RequestParam String name){

        if(name.trim().isEmpty()){
            throw new BadRequestException("Name can't be empty");
        }

        projectRepository
                .findByName(name)
                .ifPresent(project -> {
                    throw new BadRequestException(String.format("Project with name \"%s\" already exists", name));
                });

        ProjectEntity projectEntity = projectRepository.saveAndFlush(
                ProjectEntity.builder()
                        .name(name).build()
        );

        return projectDtoFactory.createProjectDto(projectEntity);
    }


    @PatchMapping(EDIT_PROJECT)
    public ProjectDto editProject(
            @PathVariable("project_id") Long project_id,
            @RequestParam String name){
        //Check if there is no name in param.
        if(name.trim().isEmpty()){
            throw new BadRequestException("Name can't be empty");
        }
        //Check for project exists in database
        ProjectEntity project = getProjectOrThrowException(project_id);

        //Check that we can assign new name to project
        projectRepository
                .findByName(name)
                .filter(anotherProj -> !Objects.equals(anotherProj.getId(), project_id))
                .ifPresent(anotherProj -> {
                    new BadRequestException(String.format("Project with name \"%s\" already exists", name));
                });

        project.setName(name);

        project = projectRepository.saveAndFlush(project);

        return projectDtoFactory.createProjectDto(project);
    }

    @DeleteMapping(DELETE_PROJECT)
    public AskDto deleteProject(@RequestParam Long id){
        ProjectEntity project = getProjectOrThrowException(id);

        return AskDto.createAnswer(true);
    }


    private ProjectEntity getProjectOrThrowException(Long id) {
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


}
