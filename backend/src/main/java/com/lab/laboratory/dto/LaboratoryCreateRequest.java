package com.lab.laboratory.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LaboratoryCreateRequest {

    @NotBlank(message = "연구실명을 입력해주세요.")
    private String labName;

    // 학과 선택 시 departmentId 사용
    private Long departmentId;

    // 기존 프론트 호환용
    private String department;

    // 책임자는 권한이 아니라 연구실 정보로만 저장
    private String managerName;

    private String location;

    private String labType;
}