package com.lab.laboratory.controller;

import com.lab.global.response.ApiResponse;
import com.lab.laboratory.dto.LaboratoryCreateRequest;
import com.lab.laboratory.dto.LaboratoryOptionResponse;
import com.lab.laboratory.dto.LaboratoryResponse;
import com.lab.laboratory.service.LaboratoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/laboratories")
public class LaboratoryController {

    private final LaboratoryService laboratoryService;

    @GetMapping
    public ApiResponse<List<LaboratoryResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String labType
    ) {
        return ApiResponse.ok("연구실 목록 조회 성공", laboratoryService.getLaboratories(keyword, departmentId, labType));
    }

    @GetMapping("/options")
    public ApiResponse<List<LaboratoryOptionResponse>> options(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long departmentId
    ) {
        return ApiResponse.ok("연구실 선택 목록 조회 성공", laboratoryService.getLaboratoryOptions(keyword, departmentId));
    }

    @GetMapping("/{id}")
    public ApiResponse<LaboratoryResponse> detail(@PathVariable Long id) {
        return ApiResponse.ok("연구실 상세 조회 성공", laboratoryService.getLaboratory(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<LaboratoryResponse> create(
            @Valid @RequestPart("data") LaboratoryCreateRequest r,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return ApiResponse.ok("연구실 등록 성공", laboratoryService.createLaboratory(r, image));
    }
}