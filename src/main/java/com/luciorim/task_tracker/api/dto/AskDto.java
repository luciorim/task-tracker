package com.luciorim.task_tracker.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AskDto {
    boolean answer;

    public static AskDto createAnswer(boolean answer){
        return AskDto.builder()
                .answer(answer)
                .build();
    }
}
