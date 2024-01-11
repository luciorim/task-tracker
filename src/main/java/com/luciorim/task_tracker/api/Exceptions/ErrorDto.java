package com.luciorim.task_tracker.api.Exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorDto {

    String error;

    @JsonProperty("error_description")
    String errorDescription;
}
