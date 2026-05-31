package com.lab.laboratory.repository;

import com.lab.laboratory.entity.LaboratoryMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LaboratoryMemberRepository extends JpaRepository<LaboratoryMember, Long> {
    boolean existsByLaboratoryIdAndUserId(Long laboratoryId, Long userId);
    List<LaboratoryMember> findByUserId(Long userId);
    List<LaboratoryMember> findByLaboratoryId(Long laboratoryId);
}
