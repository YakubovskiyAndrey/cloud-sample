package com.yakubovskyi.task.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yakubovskyi.task.dto.CreateTaskRequestDto;
import com.yakubovskyi.task.dto.UpdateTaskStatusRequestDto;
import com.yakubovskyi.task.dto.UserResponseDto;
import com.yakubovskyi.task.entity.Task;
import com.yakubovskyi.task.entity.TaskStatus;
import com.yakubovskyi.task.manager.UserManager;
import com.yakubovskyi.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TaskControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserManager userManager;

    private static final String API_URL = "/api/v1/task";

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create task successfully")
    void createTask_Success() throws Exception {
        CreateTaskRequestDto request = new CreateTaskRequestDto("user-123", "Test Task");

        mvc.perform(post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.id").isNotEmpty());

        List<Task> tasks = taskRepository.findAll();
        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getTitle()).isEqualTo("Test Task");
        assertThat(tasks.get(0).getStatus()).isEqualTo(TaskStatus.PENDING);
    }

    @Test
    @DisplayName("Should get all tasks")
    void getAllTasks_Success() throws Exception {
        taskRepository.save(Task.builder().userId("user-1").title("Task 1").status(TaskStatus.PENDING).build());
        taskRepository.save(Task.builder().userId("user-2").title("Task 2").status(TaskStatus.IN_PROGRESS).build());

        mvc.perform(get(API_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[1].title").value("Task 2"));
    }

    @Test
    @DisplayName("Should return empty list when no tasks")
    void getAllTasks_EmptyList() throws Exception {
        mvc.perform(get(API_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Should get task by id")
    void getTaskById_Success() throws Exception {
        Task savedTask = taskRepository.save(
                Task.builder().userId("user-123").title("Test Task").status(TaskStatus.PENDING).build()
        );

        mvc.perform(get(API_URL + "/{id}", savedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedTask.getId()))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("Should get task with user")
    void getTaskWithUser_Success() throws Exception {
        Task savedTask = taskRepository.save(
                Task.builder().userId("user-123").title("Test Task").status(TaskStatus.PENDING).build()
        );

        UserResponseDto mockUser = UserResponseDto.builder()
                .id("user-123")
                .name("John Doe")
                .build();
        when(userManager.getUserById(anyString())).thenReturn(mockUser);

        mvc.perform(get(API_URL + "/{id}/with-user", savedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedTask.getId()))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.user.id").value("user-123"))
                .andExpect(jsonPath("$.user.name").value("John Doe"));
    }

    @Test
    @DisplayName("Should get tasks by user id")
    void getTasksByUserId_Success() throws Exception {
        taskRepository.save(Task.builder().userId("user-123").title("Task 1").status(TaskStatus.PENDING).build());
        taskRepository.save(Task.builder().userId("user-123").title("Task 2").status(TaskStatus.COMPLETED).build());
        taskRepository.save(Task.builder().userId("user-456").title("Task 3").status(TaskStatus.PENDING).build());

        mvc.perform(get(API_URL + "/user/{userId}", "user-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value("user-123"))
                .andExpect(jsonPath("$[1].userId").value("user-123"));
    }

    @Test
    @DisplayName("Should update task status")
    void updateTaskStatus_Success() throws Exception {
        Task savedTask = taskRepository.save(
                Task.builder().userId("user-123").title("Test Task").status(TaskStatus.PENDING).build()
        );

        UpdateTaskStatusRequestDto request = UpdateTaskStatusRequestDto.builder()
                .status(TaskStatus.COMPLETED)
                .build();

        mvc.perform(patch(API_URL + "/{id}/status", savedTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedTask.getId()))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        Task updatedTask = taskRepository.findById(savedTask.getId()).orElseThrow();
        assertThat(updatedTask.getStatus()).isEqualTo(TaskStatus.COMPLETED);
    }

    @Test
    @DisplayName("Should delete task successfully")
    void deleteTask_Success() throws Exception {
        Task savedTask = taskRepository.save(
                Task.builder().userId("user-123").title("To Delete").status(TaskStatus.PENDING).build()
        );
        assertThat(taskRepository.findAll()).hasSize(1);

        mvc.perform(delete(API_URL + "/{id}", savedTask.getId()))
                .andExpect(status().isNoContent());

        assertThat(taskRepository.findAll()).isEmpty();
    }
}
