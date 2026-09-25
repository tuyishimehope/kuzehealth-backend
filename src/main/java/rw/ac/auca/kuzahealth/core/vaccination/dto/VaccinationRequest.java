package rw.ac.auca.kuzahealth.core.vaccination.dto;

import java.util.Date;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Vaccination requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VaccinationRequest {
    
    private UUID infantId;
    private UUID healthWorkerId;
    private String name;
    private String description;
    private Date administeredDate;
    private Date nextDueDate;
    private String notes;
}