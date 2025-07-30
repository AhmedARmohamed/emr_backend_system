package org.haykal.emr.mapper;

import org.haykal.emr.entity.PatientService;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapper {


    public ServiceDto toDTO(PatientService patientService) {
        if (patientService == null) {
            return null;
        }

        return new ServiceDto(
                patientService.getId(),
                patientService.getName(),
                patientService.getType(),
                patientService.getDescription(),
                patientService.getFacility().getId()
        );
    }

    public PatientService toEntity(ServiceDto serviceDTO) {
        if (serviceDTO == null) {
            return null;
        }

        return PatientService.builder()
                .id(serviceDTO.id())
                .name(serviceDTO.name())
                .type(serviceDTO.type())
                .description(serviceDTO.description())
                .build();
    }
}
