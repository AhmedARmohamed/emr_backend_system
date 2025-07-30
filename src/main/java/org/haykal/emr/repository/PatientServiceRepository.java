package org.haykal.emr.repository;

import org.haykal.emr.entity.PatientService;
import org.haykal.emr.entity.ServiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PatientServiceRepository extends JpaRepository<PatientService, Long> {
    List<PatientService> findByPatientId(Long patientId);

    Page<PatientService> findByFacilityId(Long facilityId, Pageable pageable);

    @Query("SELECT ps FROM PatientService ps WHERE ps.facility.id = :facilityId " +
            "AND ps.scheduledDate BETWEEN :startDate AND :endDate")
    List<PatientService> findByFacilityAndDateRange(@Param("facilityId") Long facilityId,
                                                    @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);

    List<PatientService> findByPatientIdAndStatus(Long patientId, ServiceStatus status);
}