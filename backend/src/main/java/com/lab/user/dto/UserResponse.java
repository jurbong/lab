package com.lab.user.dto;

import com.lab.user.entity.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String userId;
    private String name;
    private String gender;
    private Long departmentId;
    private String departmentName;
    private String departmentDisplayName;
    private AdminDepartmentType adminDepartment;
    private String adminDepartmentLabel;
    private String email;
    private String phone;
    private UserRole role;
    private String roleLabel;
    private UserStatus status;
    private String statusLabel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserResponse from(AppUser u) {
        return UserResponse.builder()
                .id(u.getId())
                .userId(u.getUserId())
                .name(u.getName())
                .gender(u.getGender())
                .departmentId(u.getDepartment() != null ? u.getDepartment().getId() : null)
                .departmentName(u.getDepartment() != null ? u.getDepartment().getName() : null)
                .departmentDisplayName(u.getDepartment() != null ? u.getDepartment().getDisplayName() : null)
                .adminDepartment(u.getAdminDepartment())
                .adminDepartmentLabel(adminDepartmentLabel(u.getAdminDepartment()))
                .email(u.getEmail())
                .phone(u.getPhone())
                .role(u.getRole())
                .roleLabel(roleLabel(u.getRole()))
                .status(u.getStatus())
                .statusLabel(statusLabel(u.getStatus()))
                .createdAt(u.getCreatedAt())
                .updatedAt(u.getUpdatedAt())
                .build();
    }

    public static String roleLabel(UserRole role) {
        if (role == null) return null;
        return switch (role) {
            case ADMIN -> "시스템 관리자";
            case GROUP_MANAGER -> "그룹 관리자";
            case SAFETY_MANAGER -> "연구실 안전관리 담당자";
            case EDUCATION_MANAGER -> "안전교육 담당자";
            case LAB_MEMBER -> "연구실 구성원";
        };
    }

    public static String statusLabel(UserStatus status) {
        if (status == null) return null;
        return switch (status) {
            case APPROVED -> "등록 완료";
            case INACTIVE -> "비활성화";
        };
    }

    public static String adminDepartmentLabel(AdminDepartmentType type) {
        if (type == null) return null;
        return switch (type) {
            case SAFETY_MANAGEMENT -> "안전관리부서";
            case EDUCATION_MANAGEMENT -> "안전교육부서";
            case SYSTEM_MANAGEMENT -> "시스템관리부서";
        };
    }
}
