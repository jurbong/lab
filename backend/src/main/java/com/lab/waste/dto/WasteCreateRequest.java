package com.lab.waste.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class WasteCreateRequest {
    @NotBlank(message = "폐기물 종류를 입력해주세요.")
    private String wasteType;

    @PositiveOrZero(message = "수량은 0 이상의 숫자만 입력할 수 있습니다.")
    private Double quantity;

    private String unit;
    private LocalDate generatedDate;
    private String storageLocation;
    private String hazardLevel;

    @NotNull(message = "연구실을 선택해주세요.")
    private Long labId;
}
