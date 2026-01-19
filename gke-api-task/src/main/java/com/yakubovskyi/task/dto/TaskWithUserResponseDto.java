package com.yakubovskyi.task.dto;

import com.yakubovskyi.task.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskWithUserResponseDto {
    private Long id;
    private String userId;
    private String title;
    private TaskStatus status;
    private UserResponseDto user;
}
