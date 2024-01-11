package com.luciorim.task_tracker.store.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "task-state")
public class TaskStateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true)
    String name;

    @Builder.Default
    Instant creationDate = Instant.now();

    Long ordinal;

    @Builder.Default
    @OneToMany
    @JoinColumn(name = "task_entity_id", referencedColumnName = "id")
    List<TaskEntity> tasks = new ArrayList<>();
}
