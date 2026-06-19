package com.lab.user.service;

import com.lab.global.exception.ApiException;
import com.lab.global.security.JwtProvider;
import com.lab.user.dto.LoginRequest;
import com.lab.user.dto.LoginResponse;
import com.lab.user.dto.UserResponse;
import com.lab.user.entity.AppUser;
import com.lab.user.entity.UserStatus;
import com.lab.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public LoginResponse login(LoginRequest r) {
        AppUser u = userRepository.findByUserId(r.getUserId())
                .orElseThrow(() -> ApiException.unauthorized("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(r.getPassword(), u.getPassword())) {
            throw ApiException.unauthorized("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        if (u.getStatus() != UserStatus.APPROVED) {
            throw ApiException.forbidden("관리자 승인 후 로그인할 수 있습니다.");
        }

        return new LoginResponse(
                jwtProvider.createToken(u),
                u.getId(),
                u.getUserId(),
                u.getName(),
                u.getRole(),
                UserResponse.roleLabel(u.getRole())
        );
    }
}
