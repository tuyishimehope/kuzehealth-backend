package rw.ac.auca.kuzahealth.controller.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailRequest {
    /** Email address or phone number of the account. */
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
