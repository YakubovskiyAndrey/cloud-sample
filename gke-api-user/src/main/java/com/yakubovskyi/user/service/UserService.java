package com.yakubovskyi.user.service;

import com.yakubovskyi.user.document.User;
import com.yakubovskyi.user.dto.CreateUserRequestDto;
import com.yakubovskyi.user.dto.UserResponseDto;
import com.yakubovskyi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDto createUser(CreateUserRequestDto request) {
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();
        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public UserResponseDto getUserById(String id) {
        User user = byIdOrThrow(id);
        return mapToResponse(user);
    }

    private User byIdOrThrow(String id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found with id: " + id));
    }

    public UserResponseDto updateUser(String id, CreateUserRequestDto request) {
        User user = byIdOrThrow(id);
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    public void deleteUser(String id) {
        userRepository.delete(byIdOrThrow(id));
    }

    private UserResponseDto mapToResponse(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
