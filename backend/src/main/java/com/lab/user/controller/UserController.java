package com.lab.user.controller;

import com.lab.global.response.ApiResponse;
import com.lab.user.dto.*;
import com.lab.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<List<UserResponse>> users(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) com.lab.user.entity.UserStatus status,
            @RequestParam(required = false) com.lab.user.entity.UserRole role,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String adminDepartment
    ) {
        return ApiResponse.ok("사용자 목록 조회 성공", userService.getUsers(keyword, status, role, departmentId, adminDepartment));
    }

    @GetMapping("/options")
    public ApiResponse<List<UserResponse>> options(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long departmentId
    ) {
        return ApiResponse.ok("사용자 선택 목록 조회 성공", userService.getUserOptions(keyword, departmentId));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> user(@PathVariable Long id) {
        return ApiResponse.ok("사용자 상세 조회 성공", userService.getUser(id));
    }

    @PostMapping
    public ApiResponse<UserResponse> create(@Valid @RequestBody UserCreateRequest r) {
        return ApiResponse.ok("사용자 등록이 완료되었습니다.", userService.createUser(r));
    }
}
