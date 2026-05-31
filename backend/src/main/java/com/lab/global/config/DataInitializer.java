package com.lab.global.config;

import com.lab.department.entity.Department;
import com.lab.department.repository.DepartmentRepository;
import com.lab.user.entity.AdminDepartmentType;
import com.lab.user.entity.AppUser;
import com.lab.user.entity.UserRole;
import com.lab.user.entity.UserStatus;
import com.lab.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        seedDepartment("공과대학", "컴퓨터공학과");
        seedDepartment("공과대학", "화학공학과");
        seedDepartment("공과대학", "기계공학과");
        seedDepartment("공과대학", "전기전자공학과");
        seedDepartment("공과대학", "생명공학과");
        seedDepartment("공과대학", "건축공학과");
        seedDepartment("공과대학", "토목공학과");
        seedDepartment("공과대학", "산업공학과");
        seedDepartment("공과대학", "환경공학과");
        seedDepartment("공과대학", "신소재공학과");

        if (!userRepository.existsByUserId("admin")) {
            userRepository.save(AppUser.builder()
                    .userId("admin")
                    .password(passwordEncoder.encode("1234"))
                    .name("관리자")
                    .gender("남")
                    .department(null)
                    .adminDepartment(AdminDepartmentType.SYSTEM_MANAGEMENT)
                    .email("admin@example.com")
                    .phone("010-0000-0000")
                    .role(UserRole.ADMIN)
                    .status(UserStatus.APPROVED)
                    .build());
        }
    }

    private void seedDepartment(String parentName, String name) {
        if (!departmentRepository.existsByName(name)) {
            departmentRepository.save(Department.builder()
                    .parentName(parentName)
                    .name(name)
                    .build());
        }
    }
}
