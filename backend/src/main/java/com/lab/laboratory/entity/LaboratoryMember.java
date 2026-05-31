package com.lab.laboratory.entity;

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
@Table(name = "laboratory_members", uniqueConstraints = @UniqueConstraint(name = "uk_lab_user", columnNames = {"lab_id", "user_id"}))
public class LaboratoryMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_id")
    private Laboratory laboratory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Enumerated(EnumType.STRING)
    private LabMemberRole memberRole;
}
