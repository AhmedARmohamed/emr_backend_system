package org.haykal.emr.repository;

import org.haykal.emr.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByMrn(String mrn);

    @Query("SELECT p FROM Patient p WHERE p.facility.id = :facilityId " +
            "AND (LOWER(p.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(p.mrn) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Patient> searchByFacility(@Param("facilityId") Long facilityId,
                                   @Param("search") String search,
                                   Pageable pageable);

    Page<Patient> findByFacilityId(Long facilityId, Pageable pageable);

    boolean existsByMrn(String mrn);

    @Query("SELECT MAX(CAST(SUBSTRING(p.mrn, 4) AS int)) FROM Patient p " +
            "WHERE p.mrn LIKE CONCAT(:prefix, '%')")
    Integer findMaxMrnNumber(@Param("prefix") String prefix);
}
