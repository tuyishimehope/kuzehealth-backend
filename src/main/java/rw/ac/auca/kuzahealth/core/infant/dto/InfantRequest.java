package rw.ac.auca.kuzahealth.core.infant.dto;

import java.util.Date;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Infant requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfantRequest {

    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String gender;

    @PositiveOrZero
    private Double birthWeight;

    @PositiveOrZero
    private Double birthHeight;

    private String bloodGroup;
    private String specialConditions;

    @NotNull
    private UUID motherId;
}
