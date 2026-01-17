package com.yakubovskyi.contoller;

import com.yakubovskyi.document.UserProfile;
import com.yakubovskyi.request.CreateUserRequestDto;
import com.yakubovskyi.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.yakubovskyi.config.RestApis.USERPROFILE;
import static com.yakubovskyi.config.RestApis.CREATE_USER;
import static com.yakubovskyi.config.RestApis.GET_ALL;

@RestController
@RequiredArgsConstructor
@RequestMapping(USERPROFILE)
public class UserProfileController {
    private final UserProfileService userProfileService;

    @PostMapping(CREATE_USER)
    public ResponseEntity<Boolean> createUser(@RequestBody CreateUserRequestDto dto) {
        userProfileService.createUser(dto);
        return ResponseEntity.ok(true);
    }

    @GetMapping(GET_ALL)
    public ResponseEntity<List<UserProfile>> getAll() {
        return ResponseEntity.ok(userProfileService.getAll());
    }
}
