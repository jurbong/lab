package com.lab.inspection.entity;
import com.lab.global.entity.BaseTimeEntity;
import com.lab.user.entity.AppUser;
import jakarta.persistence.*;
import lombok.*;
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name="inspection_forms")
public class InspectionForm extends BaseTimeEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private String formName;
    private String inspectionType;
    private String filePath;
    @Column(length=1000) private String description;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="created_by") private AppUser createdBy;
}
