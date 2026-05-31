package com.lab.education.entity;
import com.lab.global.entity.BaseTimeEntity;
import com.lab.user.entity.AppUser;
import jakarta.persistence.*;
import lombok.*;
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name="education_videos")
public class EducationVideo extends BaseTimeEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private String title;
    private String educationType;
    private String filePath;
    @Column(length=1000) private String description;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="created_by") private AppUser createdBy;
}
