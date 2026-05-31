package com.lab.laboratory.entity;

import com.lab.department.entity.Department;
import com.lab.global.entity.BaseTimeEntity;
import com.lab.user.entity.AppUser;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "laboratories")
public class Laboratory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String labName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    // 연구실 책임자는 권한(Role)이 아니라 연구실 정보로만 표시한다.
    private String managerName;

    private String location;

    private String labType;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private AppUser createdBy;
}
