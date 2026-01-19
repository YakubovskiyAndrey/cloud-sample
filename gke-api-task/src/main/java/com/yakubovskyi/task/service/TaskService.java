package com.yakubovskyi.task.service;

import com.yakubovskyi.task.dto.CreateTaskRequestDto;
import com.yakubovskyi.task.dto.TaskResponseDto;
import com.yakubovskyi.task.dto.TaskWithUserResponseDto;
import com.yakubovskyi.task.dto.UpdateTaskStatusRequestDto;
import com.yakubovskyi.task.dto.UserResponseDto;
import com.yakubovskyi.task.entity.Task;
import com.yakubovskyi.task.entity.TaskStatus;
import com.yakubovskyi.task.manager.UserManager;
import com.yakubovskyi.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserManager userManager;

    public TaskResponseDto createTask(CreateTaskRequestDto request) {
        Task task = Task.builder()
                .userId(request.getUserId())
                .title(request.getTitle())
                .status(TaskStatus.PENDING)
                .build();
        Task savedTask = taskRepository.save(task);
        return mapToResponse(savedTask);
    }

    public List<TaskResponseDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TaskResponseDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        return mapToResponse(task);
    }

    public TaskWithUserResponseDto getTaskWithUser(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        UserResponseDto user = null;
        try {
            user = userManager.getUserById(task.getUserId());
        } catch (Exception e) {
            // User service might be unavailable
        }

        return TaskWithUserResponseDto.builder()
                .id(task.getId())
                .userId(task.getUserId())
                .title(task.getTitle())
                .status(task.getStatus())
                .user(user)
                .build();
    }

    public List<TaskResponseDto> getTasksByUserId(String userId) {
        return taskRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TaskResponseDto updateTaskStatus(Long id, UpdateTaskStatusRequestDto request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        task.setStatus(request.getStatus());
        Task updatedTask = taskRepository.save(task);
        return mapToResponse(updatedTask);
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    private TaskResponseDto mapToResponse(Task task) {
        return TaskResponseDto.builder()
                .id(task.getId())
                .userId(task.getUserId())
                .title(task.getTitle())
                .status(task.getStatus())
                .build();
    }
}
