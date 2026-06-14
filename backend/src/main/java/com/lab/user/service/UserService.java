package com.lab.user.service;

import com.lab.department.entity.Department;
import com.lab.department.repository.DepartmentRepository;
import com.lab.global.exception.ApiException;
import com.lab.global.security.AuthUtil;
import com.lab.user.dto.UserCreateRequest;
import com.lab.user.dto.UserResponse;
import com.lab.user.entity.AppUser;
import com.lab.user.entity.UserRole;
import com.lab.user.entity.UserStatus;
import com.lab.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> getUsers(String keyword, UserStatus status, UserRole role, Long departmentId, String adminDepartment) {
        getCurrentAppUser();

        return userRepository.findAll().stream()
                .filter(u -> status == null || u.getStatus() == status)
                .filter(u -> role == null || u.getRole() == role)
                .filter(u -> departmentId == null || (u.getDepartment() != null && u.getDepartment().getId().equals(departmentId)))
                .filter(u -> adminDepartment == null || adminDepartment.isBlank()
                        || (u.getAdminDepartment() != null && u.getAdminDepartment().name().equalsIgnoreCase(adminDepartment)))
                .filter(u -> keyword == null || keyword.isBlank() || matchesKeyword(u, keyword))
                .map(UserResponse::from)
                .toList();
    }

    public List<UserResponse> getUserOptions(String keyword, Long departmentId) {
        getCurrentAppUser();

        return userRepository.findAll().stream()
                .filter(u -> u.getStatus() == UserStatus.APPROVED)
                .filter(u -> departmentId == null || (u.getDepartment() != null && u.getDepartment().getId().equals(departmentId)))
                .filter(u -> keyword == null || keyword.isBlank() || matchesKeyword(u, keyword))
                .map(UserResponse::from)
                .toList();
    }

    public UserResponse getUser(Long id) {
        getCurrentAppUser();
        AppUser targetUser = findUser(id);
        return UserResponse.from(targetUser);
    }

    @Transactional
    public UserResponse createUser(UserCreateRequest r) {
        requireSystemAdmin();

        if (userRepository.existsByUserId(r.getUserId())) {
            throw ApiException.badRequest("이미 사용 중인 아이디입니다.");
        }
        if (userRepository.existsByEmail(r.getEmail())) {
            throw ApiException.badRequest("이미 사용 중인 이메일입니다.");
        }

        Department department = resolveDepartment(r.getDepartmentId(), r.getDepartment());

        AppUser user = userRepository.save(AppUser.builder()
                .userId(r.getUserId())
                .password(passwordEncoder.encode(r.getPassword()))
                .name(r.getName())
                .gender(r.getGender())
                .department(department)
                .adminDepartment(r.getAdminDepartment())
                .email(r.getEmail())
                .phone(r.getPhone())
                .role(r.getRole())
                .status(UserStatus.APPROVED)
                .build());

        return UserResponse.from(user);
    }

    public AppUser findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("사용자를 찾을 수 없습니다."));
    }

    private boolean matchesKeyword(AppUser u, String keyword) {
        String k = keyword.toLowerCase();
        return contains(u.getUserId(), k)
                || contains(u.getName(), k)
                || contains(u.getEmail(), k)
                || contains(u.getPhone(), k)
                || (u.getDepartment() != null && contains(u.getDepartment().getName(), k))
                || (u.getDepartment() != null && contains(u.getDepartment().getDisplayName(), k))
                || (u.getAdminDepartment() != null && contains(u.getAdminDepartment().name(), k));
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
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

    private AppUser getCurrentAppUser() {
        return findUser(AuthUtil.getCurrentUserId());
    }

    private void requireSystemAdmin() {
        if (!AuthUtil.isSystemAdmin(AuthUtil.getCurrentUserRole())) {
            throw ApiException.forbidden("시스템 관리자 권한이 필요합니다.");
        }
    }
}
