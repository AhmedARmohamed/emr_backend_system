package org.haykal.emr.controller;

import jakarta.validation.Valid;
import org.haykal.emr.dto.*;
import org.haykal.emr.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/facilities")
@RequiredArgsConstructor
public class FacilityController {
    private final FacilityService facilityService;

    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FacilityDTO>> createFacility(@Valid @RequestBody FacilityDTO dto) {
        FacilityDTO facility = facilityService.createFacility(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Facility created successfully", facility));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FacilityDTO>> getFacility(@PathVariable Long id) {
        FacilityDTO facility = facilityService.getFacility(id);
        return ResponseEntity.ok(ApiResponse.success(facility));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FacilityDTO>>> getAllFacilities() {
        List<FacilityDTO> facilities = facilityService.getAllActiveFacilities();
        return ResponseEntity.ok(ApiResponse.success(facilities));
    }

    @PostMapping("/{facilityId}/services/{serviceTypeId}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITY_ADMIN')")
    public ResponseEntity<ApiResponse<FacilityDTO>> addServiceToFacility(
            @PathVariable Long facilityId,
            @PathVariable Long serviceTypeId) {
        FacilityDTO facility = facilityService.addServiceToFacility(facilityId, serviceTypeId);
        return ResponseEntity.ok(ApiResponse.success("Service added to facility", facility));
    }
}