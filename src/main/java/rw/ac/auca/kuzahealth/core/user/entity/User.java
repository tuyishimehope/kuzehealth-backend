package rw.ac.auca.kuzahealth.core.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import rw.ac.auca.kuzahealth.core.user.enums.EUserType;
import rw.ac.auca.kuzahealth.utils.BaseEntity;

/**
 * An account that can sign in. Never serialise this entity directly; use
 * {@code UserResponse}.
 */
@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = true)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private EUserType role;

    @Column(nullable = true)
    private String gender;

    @Column(nullable = true)
    private String province;

    @Column(nullable = true)
    private String district;

    @Column(nullable = true)
    private String sector;

    @Column(name = "date_of_birth", nullable = true)
    private String dateOfBirth;

    @Column(nullable = true)
    private String position;

    private boolean enabled = true;

    @Column(name = "phone_number")
    private String phoneNumber;

    /** BCrypt hash of the current one-time login code, or null when none is pending. */
    @JsonIgnore
    @Column(length = 512)
    private String otp;

    @JsonIgnore
    @Column(nullable = true)
    private Long otpExpirationTime;

    /** Wrong codes entered against the pending OTP. */
    @JsonIgnore
    @Column(nullable = false)
    private int otpAttempts;

    @JsonIgnore
    @Column(nullable = true)
    private String resetToken;

    @JsonIgnore
    @Column(nullable = true)
    private Long resetTokenExpiration;

    /** Access tokens issued before this instant (epoch millis) are rejected. Set on logout and password reset. */
    @JsonIgnore
    @Column(name = "tokens_invalid_before")
    private Long tokensInvalidBefore;

    public void clearOtp() {
        this.otp = null;
        this.otpExpirationTime = null;
        this.otpAttempts = 0;
    }
}
