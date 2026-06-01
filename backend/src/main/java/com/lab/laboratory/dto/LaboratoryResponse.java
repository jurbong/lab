package com.lab.laboratory.dto;

import com.lab.laboratory.entity.Laboratory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class LaboratoryResponse {
    private Long id;
    private String labName;
    private Long departmentId;
    private String departmentName;
    private String departmentDisplayName;
    private String managerName;
    private String location;
    private String labType;
    private String imageUrl;
    private Long createdBy;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<LaboratoryMemberResponse> members;

    public static LaboratoryResponse from(Laboratory l) {
        return from(l, null);
    }

    public static LaboratoryResponse from(Laboratory l, List<LaboratoryMemberResponse> members) {
        return LaboratoryResponse.builder()
                .id(l.getId())
                .labName(l.getLabName())
                .departmentId(l.getDepartment() != null ? l.getDepartment().getId() : null)
                .departmentName(l.getDepartment() != null ? l.getDepartment().getName() : null)
                .departmentDisplayName(l.getDepartment() != null ? l.getDepartment().getDisplayName() : null)
                .managerName(l.getManagerName())
                .location(l.getLocation())
                .labType(l.getLabType())
                .imageUrl(l.getImageUrl())
                .createdBy(l.getCreatedBy() != null ? l.getCreatedBy().getId() : null)
                .createdByName(l.getCreatedBy() != null ? l.getCreatedBy().getName() : null)
                .createdAt(l.getCreatedAt())
                .updatedAt(l.getUpdatedAt())
                .members(members)
                .build();
    }
}