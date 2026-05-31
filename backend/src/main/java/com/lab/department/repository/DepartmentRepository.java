package com.lab.department.repository;

import com.lab.department.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByName(String name);
    Optional<Department> findByName(String name);
    List<Department> findByNameContainingIgnoreCaseOrParentNameContainingIgnoreCase(String name, String parentName);
}
