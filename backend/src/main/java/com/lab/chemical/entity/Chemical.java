package com.lab.chemical.entity;

import com.lab.global.entity.BaseTimeEntity;
import com.lab.laboratory.entity.Laboratory;
import com.lab.user.entity.AppUser;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chemicals")
public class Chemical extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chemicalName;
    private Double quantity;
    private String unit;
    private String riskLevel;
    private String storageLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_id")
    private Laboratory laboratory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private AppUser createdBy;
}
