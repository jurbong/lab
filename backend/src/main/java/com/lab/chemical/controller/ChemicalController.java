package com.lab.chemical.controller;

import com.lab.chemical.dto.ChemicalCreateRequest;
import com.lab.chemical.dto.ChemicalResponse;
import com.lab.chemical.service.ChemicalService;
import com.lab.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chemicals")
public class ChemicalController {
    private final ChemicalService chemicalService;

    @GetMapping
    public ApiResponse<List<ChemicalResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(required = false) Long labId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String storageLocation
    ) {
        return ApiResponse.ok("화학물질 목록 조회 성공", chemicalService.getChemicals(keyword, riskLevel, labId, departmentId, storageLocation));
    }

    @GetMapping("/{id}")
    public ApiResponse<ChemicalResponse> detail(@PathVariable Long id) {
        return ApiResponse.ok("화학물질 상세 조회 성공", chemicalService.getChemical(id));
    }

    @PostMapping
    public ApiResponse<ChemicalResponse> create(@Valid @RequestBody ChemicalCreateRequest r) {
        return ApiResponse.ok("화학물질 등록 성공", chemicalService.createChemical(r));
    }
}
