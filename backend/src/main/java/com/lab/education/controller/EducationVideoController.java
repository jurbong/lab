package com.lab.education.controller;

import com.lab.education.dto.EducationVideoCreateRequest;
import com.lab.education.dto.EducationVideoResponse;
import com.lab.education.service.EducationVideoService;
import com.lab.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/education-videos")
public class EducationVideoController {

    private final EducationVideoService service;

    @GetMapping
    public ApiResponse<List<EducationVideoResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String educationType
    ) {
        return ApiResponse.ok("안전교육 동영상 목록 조회 성공", service.getVideos(keyword, educationType));
    }

    @GetMapping("/{id}")
    public ApiResponse<EducationVideoResponse> detail(@PathVariable Long id) {
        return ApiResponse.ok("안전교육 동영상 상세 조회 성공", service.getVideo(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<EducationVideoResponse> create(
            @Valid @RequestPart("data") EducationVideoCreateRequest r,
            @RequestPart(value = "video", required = false) MultipartFile video
    ) {
        return ApiResponse.ok("안전교육 동영상 등록 성공", service.createVideo(r, video));
    }
}