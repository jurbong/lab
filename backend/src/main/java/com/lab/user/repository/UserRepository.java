package com.lab.user.repository;

import com.lab.department.entity.Department;
import com.lab.user.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    boolean existsByUserId(String userId);
    boolean existsByEmail(String email);
    Optional<AppUser> findByUserId(String userId);
    List<AppUser> findByDepartment(Department department);
}
