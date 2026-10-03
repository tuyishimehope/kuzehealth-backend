package rw.ac.auca.kuzahealth.controller.auth.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.controller.auth.dto.EmailRequest;
import rw.ac.auca.kuzahealth.controller.auth.dto.LoginRequest;
import rw.ac.auca.kuzahealth.controller.auth.dto.OtpResponse;
import rw.ac.auca.kuzahealth.controller.auth.dto.RefreshRequest;
import rw.ac.auca.kuzahealth.controller.auth.dto.RegisterRequest;
import rw.ac.auca.kuzahealth.controller.auth.dto.ResetPasswordRequest;
import rw.ac.auca.kuzahealth.controller.auth.dto.UserProfileResponse;
import rw.ac.auca.kuzahealth.core.exception.BadRequestException;
import rw.ac.auca.kuzahealth.core.exception.DuplicateResourceException;
import rw.ac.auca.kuzahealth.core.healthworker.service.HealthWorkerService;
import rw.ac.auca.kuzahealth.core.user.entity.User;
import rw.ac.auca.kuzahealth.core.user.enums.EUserType;
import rw.ac.auca.kuzahealth.core.user.service.UserService;
import rw.ac.auca.kuzahealth.security.CustomUserDetails;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final HealthWorkerService healthWorkerService;

    @Value("${app.auth.open-registration:true}")
    private boolean openRegistration;

    /**
     * Anyone may register while open registration is on, but only as a health worker.
     * Other roles can be assigned only when the caller is an administrator.
     */
    @PostMapping("/register")
    public ResponseEntity<String> saveUser(@RequestBody @Valid RegisterRequest request,
            @AuthenticationPrincipal CustomUserDetails caller) {
        boolean byAdmin = caller != null && caller.hasRole(EUserType.ADMIN);
        if (!openRegistration && !byAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Registration is closed. Ask an administrator to create your account.");
        }
        EUserType role = byAdmin && request.getRole() != null ? request.getRole() : EUserType.HEALTH_WORKER;

        User registeredUser;
        try {
            registeredUser = userService.registerUser(request, role);
        } catch (DuplicateResourceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }

        if (registeredUser.getRole() == EUserType.HEALTH_WORKER) {
            try {
                healthWorkerService.createForUser(registeredUser);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("User created successfully and registered as a Health Worker.");
            } catch (RuntimeException e) {
                logger.error("Could not create health worker record for user {}", registeredUser.getId(), e);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("User created successfully but failed to register as a Health Worker.");
            }
        }

        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }

    // Step 1: check the password and send a one-time code by email and SMS
    @PostMapping("/send-otp")
    public ResponseEntity<OtpResponse> sendOtp(@RequestBody @Valid EmailRequest userRequest) {
        OtpResponse result = userService.sendOtp(userRequest);
        HttpStatus status = "SUCCESS".equals(result.getStatus()) ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return new ResponseEntity<>(result, status);
    }

    // Step 2: verify the one-time code and issue a token
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest user, @RequestParam String otp) {
        try {
            return ResponseEntity.ok(userService.verifyOtpAndLogin(user, otp));
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /** Exchanges a refresh token for a new access token. Refresh tokens are single use. */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {
        try {
            return ResponseEntity.ok(userService.refresh(request.getRefreshToken()));
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    /** Signs the caller out on every device. */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal CustomUserDetails caller) {
        userService.logout(caller.getId());
        return ResponseEntity.ok("Logged out.");
    }

    @PostMapping("/reset-password-request")
    public ResponseEntity<String> resetPasswordRequest(@RequestBody @Valid ResetPasswordRequest request) {
        userService.initiatePasswordReset(request);
        return ResponseEntity.ok("If an account exists for this email, a password reset code has been sent.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        try {
            userService.resetPassword(request);
            return ResponseEntity.ok("Successfully reset your password.");
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal CustomUserDetails caller) {
        if (caller == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        return userService.findByEmail(caller.getEmail())
                .<ResponseEntity<?>>map(user -> ResponseEntity.ok(toProfile(user)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"));
    }

    private UserProfileResponse toProfile(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .gender(user.getGender())
                .province(user.getProvince())
                .district(user.getDistrict())
                .sector(user.getSector())
                .dateOfBirth(user.getDateOfBirth())
                .position(user.getPosition())
                .build();
    }

}
