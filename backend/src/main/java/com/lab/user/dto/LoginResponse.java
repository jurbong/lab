package com.lab.user.dto;

import com.lab.user.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private Long userId;
    private String loginId;
    private String name;
    private UserRole role;
    private String roleLabel;
}
