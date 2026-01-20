package com.yakubovskyi.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yakubovskyi.user.document.User;
import com.yakubovskyi.user.dto.CreateUserRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String API_URL = "/api/v1/user";

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection(User.class);
    }

    @Test
    @DisplayName("Should create user successfully")
    void createUser_Success() throws Exception {
        CreateUserRequestDto request = CreateUserRequestDto.builder()
                .name("John Doe")
                .build();

        mvc.perform(post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.id").isNotEmpty());

        List<User> users = mongoTemplate.findAll(User.class);
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should get all users")
    void getAllUsers_Success() throws Exception {
        mongoTemplate.save(User.builder().name("User 1").build());
        mongoTemplate.save(User.builder().name("User 2").build());

        mvc.perform(get(API_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("User 1"))
                .andExpect(jsonPath("$[1].name").value("User 2"));
    }

    @Test
    @DisplayName("Should return empty list when no users")
    void getAllUsers_EmptyList() throws Exception {
        mvc.perform(get(API_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Should get user by id")
    void getUserById_Success() throws Exception {
        User savedUser = mongoTemplate.save(User.builder().name("Test User").build());

        mvc.perform(get(API_URL + "/{id}", savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    @DisplayName("Should update user successfully")
    void updateUser_Success() throws Exception {
        User savedUser = mongoTemplate.save(User.builder().name("Old Name").build());

        CreateUserRequestDto updateRequest = CreateUserRequestDto.builder()
                .name("New Name")
                .build();

        mvc.perform(put(API_URL + "/{id}", savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.name").value("New Name"));

        User updatedUser = mongoTemplate.findById(savedUser.getId(), User.class);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getName()).isEqualTo("New Name");
    }

    @Test
    @DisplayName("Should delete user successfully")
    void deleteUser_Success() throws Exception {
        User savedUser = mongoTemplate.save(User.builder().name("To Delete").build());
        assertThat(mongoTemplate.findAll(User.class)).hasSize(1);

        mvc.perform(delete(API_URL + "/{id}", savedUser.getId()))
                .andExpect(status().isNoContent());

        assertThat(mongoTemplate.findAll(User.class)).isEmpty();
    }
}
