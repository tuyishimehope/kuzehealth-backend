package rw.ac.auca.kuzahealth.controller.user.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import rw.ac.auca.kuzahealth.core.user.enums.EUserType;

/**
 * Partial update: only the fields that are present are changed.
 */
@Data
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String username;

    @Email
    private String email;

    @Size(min = 8, max = 72, message = "must be between 8 and 72 characters")
    private String password;

    /** Administrators only. */
    private EUserType role;

    /** Administrators only. */
    private Boolean enabled;

    private String gender;
    private String province;
    private String district;
    private String sector;

    @JsonProperty("date_of_Birth")
    @JsonAlias("dateOfBirth")
    private String dateOfBirth;

    private String position;
    private String phoneNumber;
}
