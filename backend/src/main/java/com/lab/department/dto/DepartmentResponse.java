package com.lab.department.dto;

import com.lab.department.entity.Department;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DepartmentResponse {
    private Long id;
    private String name;
    private String parentName;
    private String displayName;
    private String description;

    public static DepartmentResponse from(Department d) {
        return DepartmentResponse.builder()
                .id(d.getId())
                .name(d.getName())
                .parentName(d.getParentName())
                .displayName(d.getDisplayName())
                .description(d.getDescription())
                .build();
    }
}
