package com.lab.inspection.dto;

import com.lab.inspection.entity.InspectionForm;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InspectionFormResponse {
    private Long id;
    private String formName;
    private String inspectionType;
    private String filePath;
    private String description;
    private Long createdBy;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static InspectionFormResponse from(InspectionForm f) {
        return from(f, true);
    }

    public static InspectionFormResponse summary(InspectionForm f) {
        return from(f, false);
    }

    private static InspectionFormResponse from(InspectionForm f, boolean detail) {
        return InspectionFormResponse.builder()
                .id(f.getId())
                .formName(f.getFormName())
                .inspectionType(f.getInspectionType())
                .filePath(detail ? f.getFilePath() : null)
                .description(detail ? f.getDescription() : null)
                .createdBy(f.getCreatedBy() != null ? f.getCreatedBy().getId() : null)
                .createdByName(f.getCreatedBy() != null ? f.getCreatedBy().getName() : null)
                .createdAt(f.getCreatedAt())
                .updatedAt(f.getUpdatedAt())
                .build();
    }
}
