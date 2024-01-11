package com.luciorim.task_tracker.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AskDto {
    boolean answer;
    String message;

    public static AskDto createAnswer(boolean answer){
        return AskDto.builder()
                .answer(answer)
                .message(
                        answer ? "Project deleted successfully" : "Project was not deleted ")
                .build();
    }
}
