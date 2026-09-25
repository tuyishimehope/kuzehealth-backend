package rw.ac.auca.kuzahealth.core.parent.dto;

import java.util.Date;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParentRequest {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    private String email;

    @NotBlank
    private String phone;

    private String address;
    
    @NotNull
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

}
