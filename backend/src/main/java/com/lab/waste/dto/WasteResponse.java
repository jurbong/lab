package com.lab.waste.dto;
import com.lab.waste.entity.Waste;
import lombok.*;
import java.time.*;
@Getter @Builder
public class WasteResponse {
    private Long id; private String wasteType; private Double quantity; private String unit; private LocalDate generatedDate;
    private String storageLocation; private String hazardLevel; private Long labId; private String labName; private Long handlerId; private String handlerName;
    private LocalDateTime createdAt; private LocalDateTime updatedAt;
    public static WasteResponse from(Waste w){return WasteResponse.builder().id(w.getId()).wasteType(w.getWasteType()).quantity(w.getQuantity()).unit(w.getUnit()).generatedDate(w.getGeneratedDate()).storageLocation(w.getStorageLocation()).hazardLevel(w.getHazardLevel()).labId(w.getLaboratory()!=null?w.getLaboratory().getId():null).labName(w.getLaboratory()!=null?w.getLaboratory().getLabName():null).handlerId(w.getHandler()!=null?w.getHandler().getId():null).handlerName(w.getHandler()!=null?w.getHandler().getName():null).createdAt(w.getCreatedAt()).updatedAt(w.getUpdatedAt()).build();}
}
