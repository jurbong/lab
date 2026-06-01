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

    @GetMapping("/pending")
    public ApiResponse<List<UserResponse>> pending() {
        return ApiResponse.ok("승인 대기 사용자 조회 성공", userService.getPendingUsers());
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> user(@PathVariable Long id) {
        return ApiResponse.ok("사용자 상세 조회 성공", userService.getUser(id));
    }

    @PatchMapping("/{id}/approve")
    public ApiResponse<Void> approve(
            @PathVariable Long id,
            @Valid @RequestBody ApproveUserRequest r
    ) {
        userService.approveUser(id, r);
        return ApiResponse.ok("사용자 승인이 완료되었습니다.");
    }

    @PatchMapping("/{id}/reject")
    public ApiResponse<Void> reject(@PathVariable Long id) {
        userService.rejectUser(id);
        return ApiResponse.ok("사용자 반려가 완료되었습니다.");
    }
}