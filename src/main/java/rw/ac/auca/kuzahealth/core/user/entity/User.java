package rw.ac.auca.kuzahealth.core.user.entity;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import rw.ac.auca.kuzahealth.core.user.enums.EUserType;
import rw.ac.auca.kuzahealth.utils.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity implements UserDetails {

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = true)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

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

    @Column(nullable = true)
    private String date_of_Birth;

    @Column(nullable = true)
    private String position;

    private boolean enabled = true;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(length = 512)
    private String otp;

    @Column(nullable = true)
    private long otpExpirationTime;

    @Column(nullable = true)
    private String resetToken;

    @Column(nullable = true)
    private Long resetTokenExpiration;

    public User() {
    }

    public User(String firstName, String lastName, String email,
            String password, String phoneNumber, String username, long otpExpirationTime, String province,
            String district,  String sector,String date_of_Birth, String position, String gender,EUserType role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.otpExpirationTime = otpExpirationTime;
        this.province = province;
        this.district = district;
        this.sector = sector;
        this.date_of_Birth = date_of_Birth;
        this.position = position;
        this.gender = gender;
        this.role = role; // Use the role parameter instead of hardcoding HEALTH_WORKER
    }


    public static User create(String firstName, String lastName, String email,
            String password, String phoneNumber, String username, long otpExpirationTime, String province,
            String district,  String sector,String date_of_Birth, String position, String gender,EUserType role) {
        return new User(
                firstName,
                lastName,
                email,
                password, 
                phoneNumber, username, otpExpirationTime, province, district, sector, date_of_Birth, position, gender,role);
    }


    public void updateProfile(String firstName, String lastName, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public void disable() {
        this.enabled = false;
    }

    public void enable() {
        this.enabled = true;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
