package com.lab.inspection.repository;
import com.lab.inspection.entity.InspectionForm;
import org.springframework.data.jpa.repository.JpaRepository;
public interface InspectionFormRepository extends JpaRepository<InspectionForm,Long> {
    boolean existsByFormName(String formName);
}
