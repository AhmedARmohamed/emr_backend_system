package org.haykal.emr.controller;

import jakarta.validation.Valid;
import org.haykal.emr.dto.*;
import org.haykal.emr.service.PatientManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
@Validated
public class PatientController {
    private final PatientManagementService patientManagementService;

    @PostMapping
//    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITY_ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<PatientDTO>> createPatient(@Valid @RequestBody PatientDTO dto) {
        PatientDTO patient = patientManagementService.createPatient(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Patient created successfully", patient));
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITY_ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<PatientDTO>> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientDTO dto) {
        PatientDTO patient = patientManagementService.updatePatient(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Patient updated successfully", patient));
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITY_ADMIN', 'STAFF', 'VIEW_ONLY')")
    public ResponseEntity<ApiResponse<PatientDTO>> getPatient(@PathVariable Long id) {
        PatientDTO patient = patientManagementService.getPatient(id);
        return ResponseEntity.ok(ApiResponse.success(patient));
    }

    @GetMapping("/mrn/{mrn}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITY_ADMIN', 'STAFF', 'VIEW_ONLY')")
    public ResponseEntity<ApiResponse<PatientDTO>> getPatientByMrn(@PathVariable String mrn) {
        PatientDTO patient = patientManagementService.getPatientByMrn(mrn);
        return ResponseEntity.ok(ApiResponse.success(patient));
    }

    @GetMapping("/search")
//    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITY_ADMIN', 'STAFF', 'VIEW_ONLY')")
    public ResponseEntity<ApiResponse<PageResponse<PatientDTO>>> searchPatients(
            @RequestParam Long facilityId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<PatientDTO> patients = patientManagementService.searchPatients(facilityId, search, pageable);
        return ResponseEntity.ok(ApiResponse.success(patients));
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePatient(@PathVariable Long id) {
        patientManagementService.deletePatient(id);
        return ResponseEntity.ok(ApiResponse.success("Patient deleted successfully", null));
    }
}