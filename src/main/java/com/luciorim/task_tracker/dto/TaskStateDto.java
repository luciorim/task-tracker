package com.luciorim.task_tracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.antlr.v4.runtime.misc.NotNull;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskStateDto implements Serializable {

    @NotNull
    Long id;

    @NotNull
    String name;

    @NotNull
    @JsonProperty("creation_date")
    Instant creationDate = Instant.now();

    @JsonProperty("left_task_state")
    Long leftTaskStateId;

    @JsonProperty("right_tast_state")
    Long rightTaskStateId;

    @NotNull
    List<TaskDto> tasks;

}
