package com.lab.user.service;

import com.lab.global.exception.ApiException;
import com.lab.global.security.AuthUtil;
import com.lab.user.dto.ApproveUserRequest;
import com.lab.user.dto.UserResponse;
import com.lab.user.entity.AppUser;
import com.lab.user.entity.UserRole;
import com.lab.user.entity.UserStatus;
import com.lab.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> getUsers(String keyword, UserStatus status, UserRole role, Long departmentId, String adminDepartment) {
        AppUser currentUser = getCurrentAppUser();

        if (!AuthUtil.canViewUsers(currentUser.getRole())) {
            throw ApiException.forbidden("사용자 조회 권한이 필요합니다.");
        }

        return userRepository.findAll().stream()
                .filter(u -> canCurrentUserViewTarget(currentUser, u))
                .filter(u -> status == null || u.getStatus() == status)
                .filter(u -> role == null || u.getRole() == role)
                .filter(u -> departmentId == null || (u.getDepartment() != null && u.getDepartment().getId().equals(departmentId)))
                .filter(u -> adminDepartment == null || adminDepartment.isBlank()
                        || (u.getAdminDepartment() != null && u.getAdminDepartment().name().equalsIgnoreCase(adminDepartment)))
                .filter(u -> keyword == null || keyword.isBlank() || matchesKeyword(u, keyword))
                .map(UserResponse::from)
                .toList();
    }

    public List<UserResponse> getPendingUsers() {
        requireSystemAdmin();
        return userRepository.findByStatus(UserStatus.PENDING).stream()
                .map(UserResponse::from)
                .toList();
    }

    public UserResponse getUser(Long id) {
        AppUser currentUser = getCurrentAppUser();
        AppUser targetUser = findUser(id);

        if (canCurrentUserViewTarget(currentUser, targetUser)) {
            return UserResponse.from(targetUser);
        }

        throw ApiException.forbidden("사용자 상세 조회 권한이 필요합니다.");
    }

    @Transactional
    public void approveUser(Long id, ApproveUserRequest r) {
        requireSystemAdmin();

        AppUser user = findUser(id);
        user.setRole(r.getRole());
        user.setAdminDepartment(r.getAdminDepartment());
        user.setStatus(UserStatus.APPROVED);
    }

    @Transactional
    public void rejectUser(Long id) {
        requireSystemAdmin();
        AppUser user = findUser(id);
        user.setStatus(UserStatus.REJECTED);
    }

    public AppUser findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("사용자를 찾을 수 없습니다."));
    }

    private boolean canCurrentUserViewTarget(AppUser currentUser, AppUser targetUser) {
        if (AuthUtil.isSystemAdmin(currentUser.getRole())) {
            return true;
        }

        if (AuthUtil.isGroupAdmin(currentUser.getRole())) {
            return currentUser.getDepartment() != null
                    && targetUser.getDepartment() != null
                    && currentUser.getDepartment().getId().equals(targetUser.getDepartment().getId());
        }

        return currentUser.getId().equals(targetUser.getId());
    }

    private boolean matchesKeyword(AppUser u, String keyword) {
        String k = keyword.toLowerCase();
        return contains(u.getUserId(), k)
                || contains(u.getName(), k)
                || contains(u.getEmail(), k)
                || contains(u.getPhone(), k)
                || (u.getDepartment() != null && contains(u.getDepartment().getName(), k))
                || (u.getAdminDepartment() != null && contains(u.getAdminDepartment().name(), k));
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
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
