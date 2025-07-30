package org.haykal.emr.repository;

import org.haykal.emr.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {
    Optional<Facility> findByCode(String code);
    List<Facility> findByActiveTrue();
    boolean existsByCode(String code);
}
