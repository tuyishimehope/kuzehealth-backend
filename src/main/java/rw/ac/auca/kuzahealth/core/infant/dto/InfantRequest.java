package rw.ac.auca.kuzahealth.core.infant.dto;

import java.util.Date;
import java.util.UUID;

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
    private Double birthWeight;
    private Double birthHeight;
    private String bloodGroup;
    private String specialConditions;
    private UUID motherId;
}