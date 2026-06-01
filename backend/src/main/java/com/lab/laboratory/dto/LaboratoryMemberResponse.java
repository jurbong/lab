package com.lab.laboratory.dto;

import com.lab.laboratory.entity.LaboratoryMember;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LaboratoryMemberResponse {

    private Long id;
    private Long userId;
    private String loginId;
    private String name;
    private String departmentName;
    private String departmentDisplayName;
    private String memberRole;

    public static LaboratoryMemberResponse from(LaboratoryMember member) {
        return LaboratoryMemberResponse.builder()
                .id(member.getId())
                .userId(member.getUser() != null ? member.getUser().getId() : null)
                .loginId(member.getUser() != null ? member.getUser().getUserId() : null)
                .name(member.getUser() != null ? member.getUser().getName() : null)
                .departmentName(
                        member.getUser() != null && member.getUser().getDepartment() != null
                                ? member.getUser().getDepartment().getName()
                                : null
                )
                .departmentDisplayName(
                        member.getUser() != null && member.getUser().getDepartment() != null
                                ? member.getUser().getDepartment().getDisplayName()
                                : null
                )
                .memberRole(member.getMemberRole() != null ? member.getMemberRole().name() : null)
                .build();
    }
}