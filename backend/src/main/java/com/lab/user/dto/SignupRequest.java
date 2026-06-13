package com.lab.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest
{
    private String name;
    private String phone;
    private String email;
    private String role;
    private Long labId;
    private String labRole;
}
