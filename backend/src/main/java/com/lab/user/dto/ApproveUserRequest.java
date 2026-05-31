package com.lab.user.dto;

import com.lab.user.entity.AdminDepartmentType;
import com.lab.user.entity.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApproveUserRequest {

    @NotNull(message = "권한을 선택해주세요.")
    private UserRole role;

    // 안전관리/안전교육 담당자처럼 관리 부서가 필요한 경우 사용
    private AdminDepartmentType adminDepartment;
}
