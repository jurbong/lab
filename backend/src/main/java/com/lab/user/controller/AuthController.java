package com.lab.user.controller;
import com.lab.global.response.ApiResponse;
import com.lab.user.dto.*;
import com.lab.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController @RequiredArgsConstructor @RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    @PostMapping("/signup") public ApiResponse<Void> signup(@Valid @RequestBody SignupRequest r){authService.signup(r); return ApiResponse.ok("회원가입 신청이 완료되었습니다.");}
    @PostMapping("/login") public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest r){return ApiResponse.ok("로그인 성공",authService.login(r));}
}
