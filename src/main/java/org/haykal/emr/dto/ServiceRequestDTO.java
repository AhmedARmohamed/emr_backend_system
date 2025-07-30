package org.haykal.emr.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ServiceRequestDTO {
    @NotNull(message = "Service type ID is required")
    private Long serviceTypeId;

    private LocalDateTime scheduledDate;
    private String notes;
    private String providerName;
}