package rw.ac.auca.kuzahealth.controller.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    /** Email address or phone number of the account. */
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
