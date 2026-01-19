package com.yakubovskyi.task.controller;

import com.yakubovskyi.task.config.RestApis;
import com.yakubovskyi.task.dto.CreateTaskRequestDto;
import com.yakubovskyi.task.dto.TaskResponseDto;
import com.yakubovskyi.task.dto.TaskWithUserResponseDto;
import com.yakubovskyi.task.dto.UpdateTaskStatusRequestDto;
import com.yakubovskyi.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(RestApis.TASK)
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(@RequestBody CreateTaskRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(request));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping("/{id}/with-user")
    public ResponseEntity<TaskWithUserResponseDto> getTaskWithUser(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskWithUser(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskResponseDto>> getTasksByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(taskService.getTasksByUserId(userId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponseDto> updateTaskStatus(@PathVariable Long id, @RequestBody UpdateTaskStatusRequestDto request) {
        return ResponseEntity.ok(taskService.updateTaskStatus(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
