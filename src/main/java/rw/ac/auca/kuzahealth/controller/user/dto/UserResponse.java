package rw.ac.auca.kuzahealth.controller.user.dto;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import rw.ac.auca.kuzahealth.core.user.entity.User;
import rw.ac.auca.kuzahealth.core.user.enums.EUserType;

/**
 * What the API exposes about an account: no password hash, login code or reset token.
 */
@Data
@Builder
public class UserResponse {
    private UUID id;
    private Date createdAt;
    private Date updatedAt;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private EUserType role;
    private String gender;
    private String province;
    private String district;
    private String sector;

    @JsonProperty("date_of_Birth")
    private String dateOfBirth;

    private String position;
    private boolean enabled;
    private String phoneNumber;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .gender(user.getGender())
                .province(user.getProvince())
                .district(user.getDistrict())
                .sector(user.getSector())
                .dateOfBirth(user.getDateOfBirth())
                .position(user.getPosition())
                .enabled(user.isEnabled())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
