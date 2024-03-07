package com.luciorim.task_tracker.repositories;

import com.luciorim.task_tracker.entities.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.stream.Stream;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    Stream<TaskEntity> streamAllByTaskStateIdAndNameStartsWithIgnoreCase(Long id, String prefix);

    Stream<TaskEntity> streamAllByTaskStateId(Long id);

    Optional<TaskEntity> findTaskEntityByTaskStateIdAndNameContainsIgnoreCase(Long task_state_id, String task_name);
}
