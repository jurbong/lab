package com.lab.department.entity;

import com.lab.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "departments", uniqueConstraints = @UniqueConstraint(name = "uk_department_name", columnNames = "name"))
public class Department extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 50)
    private String parentName;

    @Column(length = 200)
    private String description;

    public String getDisplayName() {
        if (parentName == null || parentName.isBlank()) {
            return name;
        }
        return parentName + " - " + name;
    }
}
