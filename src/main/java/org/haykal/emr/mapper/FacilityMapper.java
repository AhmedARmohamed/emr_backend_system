package org.haykal.emr.mapper;

import org.haykal.emr.dto.FacilityDto;
import org.haykal.emr.entity.Facility;
import org.springframework.stereotype.Component;

@Component
public class FacilityMapper {

    public FacilityDto toDto(Facility facility) {
        if (facility == null) {
            return null;
        }

        return new FacilityDto(
            facility.getId(),
            facility.getName(),
            facility.getAddress(),
            facility.getPhone(),
            facility.getEmail(),
            facility.getType(),
            facility.getCreatedAt()
        );
    }

    public Facility toEntity(FacilityDto facilityDto) {
        if (facilityDto == null) {
            return null;
        }

        return Facility.builder()
            .id(facilityDto.id())
            .name(facilityDto.name())
            .address(facilityDto.address())
            .phone(facilityDto.phone())
            .email(facilityDto.email())
            .type(facilityDto.type())
            .createdAt(facilityDto.createdAt())
            .build();
    }
}
