package com.lab.user.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
@Getter @Setter
public class LoginRequest { @NotBlank private String userId; @NotBlank private String password; }
