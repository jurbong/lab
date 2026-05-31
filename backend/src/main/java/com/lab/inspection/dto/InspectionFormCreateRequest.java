package com.lab.inspection.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InspectionFormCreateRequest {

    @NotBlank(message = "점검 양식명을 입력해주세요.")
    private String formName;

    private String inspectionType;

    private String description;
}