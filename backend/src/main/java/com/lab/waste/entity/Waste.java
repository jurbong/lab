package com.lab.waste.entity;
import com.lab.global.entity.BaseTimeEntity;
import com.lab.laboratory.entity.Laboratory;
import com.lab.user.entity.AppUser;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name="wastes")
public class Waste extends BaseTimeEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private String wasteType;
    private Double quantity;
    private String unit;
    private LocalDate generatedDate;
    private String storageLocation;
    private String hazardLevel;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="lab_id") private Laboratory laboratory;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="handler_id") private AppUser handler;
}
