package rw.ac.auca.kuzahealth.controller.auth.dto;

import lombok.Data;

@Data
public class EmailRequest {
    private String email;
    private String password;
}
