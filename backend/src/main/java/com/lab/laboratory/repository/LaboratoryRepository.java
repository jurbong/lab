package com.lab.laboratory.repository;
import com.lab.laboratory.entity.Laboratory;
import org.springframework.data.jpa.repository.JpaRepository;
public interface LaboratoryRepository extends JpaRepository<Laboratory,Long> {
    boolean existsByLabName(String labName);
}
