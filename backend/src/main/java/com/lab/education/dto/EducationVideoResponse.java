package com.lab.education.dto;

import com.lab.education.entity.EducationVideo;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class EducationVideoResponse {
    private Long id;
    private String title;
    private String educationType;
    private String filePath;
    private String description;
    private Long createdBy;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static EducationVideoResponse from(EducationVideo v) {
        return from(v, true);
    }

    public static EducationVideoResponse summary(EducationVideo v) {
        return from(v, false);
    }

    private static EducationVideoResponse from(EducationVideo v, boolean detail) {
        return EducationVideoResponse.builder()
                .id(v.getId())
                .title(v.getTitle())
                .educationType(v.getEducationType())
                .filePath(detail ? v.getFilePath() : null)
                .description(detail ? v.getDescription() : null)
                .createdBy(v.getCreatedBy() != null ? v.getCreatedBy().getId() : null)
                .createdByName(v.getCreatedBy() != null ? v.getCreatedBy().getName() : null)
                .createdAt(v.getCreatedAt())
                .updatedAt(v.getUpdatedAt())
                .build();
    }
}
