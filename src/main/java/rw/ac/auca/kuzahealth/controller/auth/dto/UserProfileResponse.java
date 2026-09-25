package rw.ac.auca.kuzahealth.controller.auth.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rw.ac.auca.kuzahealth.core.user.enums.EUserType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phoneNumber;
    private EUserType role;
    private String gender;
    private String province;
    private String district;
    private String sector;
    private String dateOfBirth;
    private String position;
}
