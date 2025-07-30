package org.haykal.emr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.haykal.emr.dto.PatientDto;
import org.haykal.emr.mapper.PatientMapper;
import org.haykal.emr.repository.FacilityRepository;
import org.haykal.emr.repository.PatientRepository;
import org.haykal.emr.repository.ServiceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;
    private final FacilityRepository facilityRepository;
    private final ServiceRepository serviceRepository;
    private final PatientMapper patientMapper;

    @Transactional(readOnly = true)
    public Page<PatientDto> getAllPatients(Pageable pageable) {
        log.debug("Request to get all patients");
        return patientRepository.findAll(pageable).map(patientMapper::toDTO);
    }


}
