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

    // PostgreSQL compatible query for extracting number from MRN
    @Query(value = "SELECT COALESCE(MAX(CAST(SUBSTRING(mrn FROM :prefixLength + 1) AS INTEGER)), 0) " +
            "FROM patients WHERE mrn LIKE CONCAT(:prefix, '%')",
            nativeQuery = true)
    Integer findMaxMrnNumber(@Param("prefix") String prefix, @Param("prefixLength") int prefixLength);

    // Alternative HQL version if you prefer
    @Query("SELECT COALESCE(MAX(CAST(FUNCTION('SUBSTRING', p.mrn, :prefixLength + 1) AS INTEGER)), 0) " +
            "FROM Patient p WHERE p.mrn LIKE CONCAT(:prefix, '%')")
    Integer findMaxMrnNumberHQL(@Param("prefix") String prefix, @Param("prefixLength") int prefixLength);
}