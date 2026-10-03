package rw.ac.auca.kuzahealth.core.parent.dto;

import java.util.Date;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import rw.ac.auca.kuzahealth.core.parent.enums.Language;

@Getter
@Setter
public class ParentRequest {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    private String email;

    private String phone;

    private Date expectedDeliveryDate;
    private boolean highRisk;
    private String bloodGroup;
    private String maritalStatus;
    private String emergencyContactNumber;
    private String emergencyContactFullName;
    private String emergencyContactRelationship;
    private String district;
    private String sector;
    private String cell;
    private String village;

    /** Optional. Left unchanged when absent. */
    private Boolean smsConsent;

    /** Optional: EN or RW. Left unchanged when absent. */
    private Language preferredLanguage;
}
