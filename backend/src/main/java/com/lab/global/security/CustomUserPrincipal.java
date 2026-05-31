package com.lab.global.security;

import com.lab.user.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomUserPrincipal {
    private Long id;
    private String userId;
    private String name;
    private UserRole role;
    private Long departmentId;
    private String departmentName;
}
