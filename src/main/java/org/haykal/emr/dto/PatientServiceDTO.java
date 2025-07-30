package org.haykal.emr.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PatientServiceDTO {
    private Long id;
    private Long patientId;
    private String patientName;
    private String patientMrn;
    private Long serviceTypeId;
    private String serviceTypeName;
    private String serviceCategory;
    private Long facilityId;
    private String facilityName;
    private String status;
    private LocalDateTime scheduledDate;
    private LocalDateTime completedDate;
    private String notes;
    private String providerName;
    private LocalDateTime createdAt;
}