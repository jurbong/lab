package com.lab.department.service;

import com.lab.department.dto.DepartmentResponse;
import com.lab.department.entity.Department;
import com.lab.department.repository.DepartmentRepository;
import com.lab.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public List<DepartmentResponse> getDepartments(String keyword) {
        List<Department> departments = (keyword == null || keyword.isBlank())
                ? departmentRepository.findAll()
                : departmentRepository.findByNameContainingIgnoreCaseOrParentNameContainingIgnoreCase(keyword, keyword);

        return departments.stream()
                .sorted(Comparator.comparing(Department::getDisplayName))
                .map(DepartmentResponse::from)
                .toList();
    }

    public Department findDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("학과를 찾을 수 없습니다."));
    }

    public Department findByName(String name) {
        return departmentRepository.findByName(name)
                .orElseThrow(() -> ApiException.notFound("학과를 찾을 수 없습니다."));
    }
}
