package com.luciorim.task_tracker.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
