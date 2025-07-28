package org.haykal.emr.repository;

import org.haykal.emr.entity.Facility;
import org.haykal.emr.entity.FacilityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, UUID> {

    List<Facility> findByType(FacilityType type);

    @Query("SELECT f FROM Facility f ORDER BY f.name")
    List<Facility> findAllOrderByName();

    boolean existsByNameAndType(String name, FacilityType type);
}
