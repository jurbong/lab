package com.lab.global.security;

import com.lab.global.exception.ApiException;
import com.lab.user.entity.UserRole;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {

    public static CustomUserPrincipal getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserPrincipal principal)) {
            throw ApiException.unauthorized("로그인이 필요합니다.");
        }
        return principal;
    }

    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public static UserRole getCurrentUserRole() {
        return getCurrentUser().getRole();
    }

    public static boolean isSystemAdmin(UserRole role) {
        return role == UserRole.ADMIN;
    }

    public static boolean isGroupAdmin(UserRole role) {
        return role == UserRole.GROUP_MANAGER;
    }

    public static boolean isSafetyManager(UserRole role) {
        return role == UserRole.SAFETY_MANAGER;
    }

    public static boolean isEducationManager(UserRole role) {
        return role == UserRole.EDUCATION_MANAGER;
    }

    public static boolean isLabMember(UserRole role) {
        return role == UserRole.LAB_MEMBER;
    }

    public static boolean canViewUsers(UserRole role) {
        return role == UserRole.ADMIN || role == UserRole.GROUP_MANAGER;
    }

    public static boolean isAdmin(UserRole role) {
        return isSystemAdmin(role);
    }
}
