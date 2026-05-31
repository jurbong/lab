package com.lab.department.controller;

import com.lab.department.dto.DepartmentResponse;
import com.lab.department.service.DepartmentService;
import com.lab.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public ApiResponse<List<DepartmentResponse>> list(@RequestParam(required = false) String keyword) {
        return ApiResponse.ok("학과 목록 조회 성공", departmentService.getDepartments(keyword));
    }
}
