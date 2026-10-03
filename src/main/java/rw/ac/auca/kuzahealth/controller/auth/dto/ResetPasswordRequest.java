package rw.ac.auca.kuzahealth.controller.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank
    @Email
    private String email;
    private String password;
    private String confirmPassword;
    private String token;
}
