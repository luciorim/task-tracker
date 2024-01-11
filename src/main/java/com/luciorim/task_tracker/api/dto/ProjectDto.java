package com.luciorim.task_tracker.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectDto {
    @NotNull
    Long id;

    @NotNull
    String name;

    @NotNull
    @JsonProperty("last_update_date")
    Instant lastUpdate;

    @NotNull
    @JsonProperty("creation_date")
    Instant creationDate;
}
