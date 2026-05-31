package com.lab.waste.controller;

import com.lab.global.response.ApiResponse;
import com.lab.waste.dto.WasteCreateRequest;
import com.lab.waste.dto.WasteResponse;
import com.lab.waste.service.WasteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wastes")
public class WasteController {
    private final WasteService wasteService;

    @GetMapping
    public ApiResponse<List<WasteResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String wasteType,
            @RequestParam(required = false) String hazardLevel,
            @RequestParam(required = false) Long labId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String unit
    ) {
        return ApiResponse.ok("폐기물 목록 조회 성공", wasteService.getWastes(keyword, wasteType, hazardLevel, labId, departmentId, unit));
    }

    @GetMapping("/{id}")
    public ApiResponse<WasteResponse> detail(@PathVariable Long id) {
        return ApiResponse.ok("폐기물 상세 조회 성공", wasteService.getWaste(id));
    }

    @PostMapping
    public ApiResponse<WasteResponse> create(@Valid @RequestBody WasteCreateRequest r) {
        return ApiResponse.ok("폐기물 등록 성공", wasteService.createWaste(r));
    }
}
