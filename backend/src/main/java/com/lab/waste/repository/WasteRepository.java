package com.lab.waste.repository;
import com.lab.waste.entity.Waste;
import org.springframework.data.jpa.repository.JpaRepository;
public interface WasteRepository extends JpaRepository<Waste,Long> {}
