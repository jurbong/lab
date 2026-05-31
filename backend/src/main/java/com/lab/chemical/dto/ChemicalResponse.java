package com.lab.chemical.dto;

import com.lab.chemical.entity.Chemical;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChemicalResponse {
    private Long id;
    private String chemicalName;
    private Double quantity;
    private String unit;
    private String riskLevel;
    private String storageLocation;
    private Long labId;
    private String labName;
    private String departmentName;
    private String managerName;
    private Long createdBy;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ChemicalResponse from(Chemical c) {
        return ChemicalResponse.builder()
                .id(c.getId())
                .chemicalName(c.getChemicalName())
                .quantity(c.getQuantity())
                .unit(c.getUnit())
                .riskLevel(c.getRiskLevel())
                .storageLocation(c.getStorageLocation())
                .labId(c.getLaboratory() != null ? c.getLaboratory().getId() : null)
                .labName(c.getLaboratory() != null ? c.getLaboratory().getLabName() : null)
                .departmentName(c.getLaboratory() != null && c.getLaboratory().getDepartment() != null ? c.getLaboratory().getDepartment().getName() : null)
                .managerName(c.getLaboratory() != null ? c.getLaboratory().getManagerName() : null)
                .createdBy(c.getCreatedBy() != null ? c.getCreatedBy().getId() : null)
                .createdByName(c.getCreatedBy() != null ? c.getCreatedBy().getName() : null)
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
