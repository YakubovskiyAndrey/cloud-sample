package com.yakubovskyi.service;

import com.yakubovskyi.dto.request.CreateUserRequestDto;
import com.yakubovskyi.dto.request.LoginResponseDto;
import com.yakubovskyi.dto.request.RegisterRequestDto;
import com.yakubovskyi.entity.Auth;
import com.yakubovskyi.manager.UserProfileManager;
import com.yakubovskyi.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepository repository;
    private final UserProfileManager userProfileManager;

    public Auth register(RegisterRequestDto dto) {
        Auth auth = repository.save(Auth.builder()
                        .userName(dto.getUserName())
                        .email(dto.getEmail())
                        .password(dto.getPassword())
                .build());
        userProfileManager.createUser(CreateUserRequestDto.builder()
                        .authId(auth.getId())
                        .email(auth.getEmail())
                        .username(auth.getUserName())
                .build());
        return auth;
    }

    public Boolean login(LoginResponseDto dto) {
        return repository.existsByUserNameAndPassword(dto.getUserName(),dto.getPassword());
    }
}
