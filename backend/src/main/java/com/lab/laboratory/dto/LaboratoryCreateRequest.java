package com.lab.laboratory.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LaboratoryCreateRequest {

    @NotBlank(message = "연구실명을 입력해주세요.")
    private String labName;

    private Long departmentId;

    private String department;

    private String managerName;

    private Long managerId;

    private List<Long> memberIds;

    private String location;

    private String labType;
}