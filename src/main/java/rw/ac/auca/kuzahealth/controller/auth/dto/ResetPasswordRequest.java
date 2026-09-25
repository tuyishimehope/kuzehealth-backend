package rw.ac.auca.kuzahealth.controller.auth.dto;

import lombok.Data;

import java.util.Optional;

@Data
public class ResetPasswordRequest {
    private String email;
    private Optional<String> password;
    private Optional<String> confirmPassword;
    private Optional<String> token;
}
