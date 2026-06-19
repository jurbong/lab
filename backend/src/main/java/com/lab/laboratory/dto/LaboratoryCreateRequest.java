package com.lab.laboratory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LaboratoryCreateRequest {

    @NotBlank(message = "연구실명을 입력해주세요.")
    private String labName;

    @NotNull(message = "학과를 선택해주세요.")
    private Long departmentId;

    private String department;

    private String managerName;

    @NotNull(message = "책임자를 선택해주세요.")
    private Long managerId;

    @NotEmpty(message = "구성원을 1명 이상 선택해주세요.")
    private List<Long> memberIds;

    @NotBlank(message = "위치를 입력해주세요.")
    private String location;

    @NotBlank(message = "연구실 타입을 입력해주세요.")
    private String labType;
}
