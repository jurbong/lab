package com.lab.education.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EducationVideoCreateRequest {

    @NotBlank(message = "교육 동영상 제목을 입력해주세요.")
    private String title;

    private String educationType;

    private String description;
}