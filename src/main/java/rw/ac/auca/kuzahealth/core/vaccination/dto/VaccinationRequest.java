package rw.ac.auca.kuzahealth.core.vaccination.dto;

import java.util.Date;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private UUID infantId;

    @NotNull
    private UUID healthWorkerId;

    /** Free-text vaccine name. Optional when scheduleCode is given. */
    private String name;

    /** Code of the routine schedule item this dose fulfils, e.g. PENTA2. */
    private String scheduleCode;

    private String description;

    @NotNull
    private Date administeredDate;

    private Date nextDueDate;
    private String notes;
}
