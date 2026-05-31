package com.lab.education.repository;
import com.lab.education.entity.EducationVideo;
import org.springframework.data.jpa.repository.JpaRepository;
public interface EducationVideoRepository extends JpaRepository<EducationVideo,Long> {
    boolean existsByTitle(String title);
}
