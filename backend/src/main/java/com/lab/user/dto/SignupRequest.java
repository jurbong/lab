package com.lab.user.dto;

import com.lab.user.entity.AdminDepartmentType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {

    @NotBlank(message = "아이디를 입력해주세요.")
    private String userId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    private String gender;

    // 권장: 학과 선택 시 departmentId 사용
    private Long departmentId;

    // 기존 프론트 호환용: 학과명을 보내도 처리 가능
    private String department;

    // 관리 부서 선택 시 사용. 일반 연구실 구성원은 null 가능
    private AdminDepartmentType adminDepartment;

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @Pattern(
            regexp = "^010-\\d{4}-\\d{4}$",
            message = "전화번호는 010-0000-0000 형식으로 입력해주세요."
    )
    private String phone;
}
