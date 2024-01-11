package com.luciorim.task_tracker.store.repositories;

import com.luciorim.task_tracker.store.entities.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskStateRepository extends JpaRepository<TaskEntity, Long> {
}
