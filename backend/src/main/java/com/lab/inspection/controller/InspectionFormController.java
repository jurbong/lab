package com.lab.inspection.controller;

import com.lab.global.response.ApiResponse;
import com.lab.inspection.dto.InspectionFormCreateRequest;
import com.lab.inspection.dto.InspectionFormResponse;
import com.lab.inspection.service.InspectionFormService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inspection-forms")
public class InspectionFormController {

    private final InspectionFormService service;

    @GetMapping
    public ApiResponse<List<InspectionFormResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String inspectionType
    ) {
        return ApiResponse.ok("점검 양식 목록 조회 성공", service.getForms(keyword, inspectionType));
    }

    @GetMapping("/{id}")
    public ApiResponse<InspectionFormResponse> detail(@PathVariable Long id) {
        return ApiResponse.ok("점검 양식 상세 조회 성공", service.getForm(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<InspectionFormResponse> create(
            @Valid @RequestPart("data") InspectionFormCreateRequest r,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return ApiResponse.ok("점검 양식 등록 성공", service.createForm(r, file));
    }
}