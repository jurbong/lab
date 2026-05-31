package com.lab.chemical.repository;
import com.lab.chemical.entity.Chemical;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ChemicalRepository extends JpaRepository<Chemical,Long> {}
