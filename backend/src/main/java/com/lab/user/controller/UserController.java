package com.lab.user.controller;

import com.lab.global.response.ApiResponse;
import com.lab.user.dto.RegisterRequest;
import com.lab.user.entity.User;
import com.lab.user.entity.UserRole;
import com.lab.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ApiResponse<User> signup(@RequestBody RegisterRequest request)
    {
        User user = User.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .role(UserRole.valueOf(request.getRole()))
                .labId(request.getLabId())
                .labRole(request.getLabRole())
                .build();

        User savedUser = userService.registerUser(user);

        return ApiResponse.ok("사용자 등록이 완료되었습니다.", savedUser);
    }

    @GetMapping("/users")
    public ApiResponse<List<User>> searchUsers()
    {
        List<User> users = userService.findAllUsers();

        return ApiResponse.ok("사용자 전체 조회 성공", users);
    }
}
