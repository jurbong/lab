package com.lab.chemical.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChemicalCreateRequest {
    @NotBlank(message = "화학물질명을 입력해주세요.")
    private String chemicalName;

    @PositiveOrZero(message = "보유량은 0 이상의 숫자만 입력할 수 있습니다.")
    private Double quantity;

    private String unit;
    private String riskLevel;
    private String storageLocation;

    @NotNull(message = "연구실을 선택해주세요.")
    private Long labId;
}
