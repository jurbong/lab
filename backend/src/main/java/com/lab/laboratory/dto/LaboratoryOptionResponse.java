package com.lab.laboratory.dto;

import com.lab.laboratory.entity.Laboratory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LaboratoryOptionResponse {
    private Long id;
    private String labName;
    private String departmentName;
    private String departmentDisplayName;
    private String managerName;
    private String label;

    public static LaboratoryOptionResponse from(Laboratory l) {
        String department = l.getDepartment() != null ? l.getDepartment().getName() : "학과 없음";
        String manager = l.getManagerName() != null && !l.getManagerName().isBlank() ? l.getManagerName() : "책임자 미지정";
        return LaboratoryOptionResponse.builder()
                .id(l.getId())
                .labName(l.getLabName())
                .departmentName(l.getDepartment() != null ? l.getDepartment().getName() : null)
                .departmentDisplayName(l.getDepartment() != null ? l.getDepartment().getDisplayName() : null)
                .managerName(l.getManagerName())
                .label(l.getLabName() + " / " + department + " / " + manager)
                .build();
    }
}
