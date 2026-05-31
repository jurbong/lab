package com.lab.user.service;

import com.lab.department.entity.Department;
import com.lab.department.repository.DepartmentRepository;
import com.lab.global.exception.ApiException;
import com.lab.global.security.JwtProvider;
import com.lab.user.dto.LoginRequest;
import com.lab.user.dto.LoginResponse;
import com.lab.user.dto.SignupRequest;
import com.lab.user.dto.UserResponse;
import com.lab.user.entity.AppUser;
import com.lab.user.entity.UserRole;
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
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public void signup(SignupRequest r) {
        if (userRepository.existsByUserId(r.getUserId())) {
            throw ApiException.badRequest("이미 사용 중인 아이디입니다.");
        }
        if (userRepository.existsByEmail(r.getEmail())) {
            throw ApiException.badRequest("이미 사용 중인 이메일입니다.");
        }

        Department department = resolveDepartment(r.getDepartmentId(), r.getDepartment());

        userRepository.save(AppUser.builder()
                .userId(r.getUserId())
                .password(passwordEncoder.encode(r.getPassword()))
                .name(r.getName())
                .gender(r.getGender())
                .department(department)
                .adminDepartment(r.getAdminDepartment())
                .email(r.getEmail())
                .phone(r.getPhone())
                .role(UserRole.LAB_MEMBER)
                .status(UserStatus.PENDING)
                .build());
    }

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

    private Department resolveDepartment(Long departmentId, String departmentName) {
        if (departmentId != null) {
            return departmentRepository.findById(departmentId)
                    .orElseThrow(() -> ApiException.badRequest("선택한 학과를 찾을 수 없습니다."));
        }
        if (departmentName != null && !departmentName.isBlank()) {
            return departmentRepository.findByName(departmentName)
                    .orElseThrow(() -> ApiException.badRequest("선택한 학과를 찾을 수 없습니다."));
        }
        return null;
    }
}
