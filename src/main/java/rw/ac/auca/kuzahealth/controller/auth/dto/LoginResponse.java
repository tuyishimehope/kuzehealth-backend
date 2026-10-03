package rw.ac.auca.kuzahealth.controller.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String refreshToken;
    /** Lifetime of the access token in seconds. */
    private Long expiresIn;
    private String email;
    private String userType;
    private String message;
}
