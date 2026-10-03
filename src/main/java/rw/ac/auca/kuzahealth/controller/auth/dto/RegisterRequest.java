package rw.ac.auca.kuzahealth.controller.auth.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import rw.ac.auca.kuzahealth.core.user.enums.EUserType;

@Data
public class RegisterRequest {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 72, message = "must be between 8 and 72 characters")
    private String password;

    /** Only honoured when the caller is an administrator. */
    @JsonAlias("userType")
    private EUserType role;

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
