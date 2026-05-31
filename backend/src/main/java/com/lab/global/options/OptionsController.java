package com.lab.global.options;

import com.lab.global.response.ApiResponse;
import com.lab.user.dto.UserResponse;
import com.lab.user.entity.AdminDepartmentType;
import com.lab.user.entity.UserRole;
import com.lab.user.entity.UserStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Arrays;

@RestController
@RequestMapping("/api/options")
public class OptionsController {

    @GetMapping("/roles")
    public ApiResponse<List<OptionResponse>> roles() {
        return ApiResponse.ok("권한 옵션 조회 성공", Arrays.stream(UserRole.values())
                .map(r -> new OptionResponse(r.name(), UserResponse.roleLabel(r)))
                .toList());
    }

    @GetMapping("/statuses")
    public ApiResponse<List<OptionResponse>> statuses() {
        return ApiResponse.ok("상태 옵션 조회 성공", Arrays.stream(UserStatus.values())
                .map(s -> new OptionResponse(s.name(), UserResponse.statusLabel(s)))
                .toList());
    }

    @GetMapping("/admin-departments")
    public ApiResponse<List<OptionResponse>> adminDepartments() {
        return ApiResponse.ok("관리 부서 옵션 조회 성공", Arrays.stream(AdminDepartmentType.values())
                .map(d -> new OptionResponse(d.name(), UserResponse.adminDepartmentLabel(d)))
                .toList());
    }

    @GetMapping("/units")
    public ApiResponse<List<OptionResponse>> units() {
        return ApiResponse.ok("단위 옵션 조회 성공", List.of(
                new OptionResponse("kg", "kg"),
                new OptionResponse("g", "g"),
                new OptionResponse("L", "L"),
                new OptionResponse("mL", "mL"),
                new OptionResponse("개", "개"),
                new OptionResponse("통", "통")
        ));
    }

    @GetMapping("/risk-levels")
    public ApiResponse<List<OptionResponse>> riskLevels() {
        return ApiResponse.ok("위험/유해성 등급 옵션 조회 성공", List.of(
                new OptionResponse("LOW", "낮음"),
                new OptionResponse("MEDIUM", "보통"),
                new OptionResponse("HIGH", "높음"),
                new OptionResponse("DANGER", "위험")
        ));
    }
}
