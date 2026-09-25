package rw.ac.auca.kuzahealth.controller.auth.api;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.controller.auth.dto.EmailRequest;
import rw.ac.auca.kuzahealth.controller.auth.dto.OtpResponse;
import rw.ac.auca.kuzahealth.controller.auth.dto.ResetPasswordRequest;
import rw.ac.auca.kuzahealth.controller.user.UserController;
import rw.ac.auca.kuzahealth.core.healthworker.entity.HealthWorker;
import rw.ac.auca.kuzahealth.core.healthworker.service.HealthWorkerService;
import rw.ac.auca.kuzahealth.core.user.entity.User;
import rw.ac.auca.kuzahealth.core.user.enums.EUserType;
import rw.ac.auca.kuzahealth.core.user.service.UserService;
import rw.ac.auca.kuzahealth.controller.auth.dto.LoginResponse;
import rw.ac.auca.kuzahealth.controller.auth.dto.UserProfileResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import rw.ac.auca.kuzahealth.security.CustomUserDetails;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private HealthWorkerService healthWorkerService;

    @PostMapping("/register")
    public ResponseEntity<String> saveUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            logger.info("Registered user role: {}", registeredUser.getRole());

            // If the user has the HEALTH_WORKER role, also create a HealthWorker entity
            if (registeredUser.getRole() == EUserType.HEALTH_WORKER) {
                logger.info("Creating HealthWorker entity for user with HEALTH_WORKER role");

                HealthWorker healthWorker = new HealthWorker();
                healthWorker.setFirst_name(registeredUser.getFirstName());
                healthWorker.setLast_name(registeredUser.getLastName());
                healthWorker.setEmail(registeredUser.getEmail());
                healthWorker.setPhone_number(registeredUser.getPhoneNumber());

                // Set the reference to the User entity
                healthWorker.setUser(registeredUser);

                // Save the HealthWorker entity
                try {
                    HealthWorker createdHealthWorker = healthWorkerService.createHealthWorker(healthWorker);
                    logger.info("HealthWorker created successfully: {}", createdHealthWorker.getId());
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body("User created successfully and registered as a Health Worker.");
                } catch (Exception e) {
                    logger.error("Error creating HealthWorker: {}", e.getMessage());
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body("User created successfully but failed to register as a Health Worker: " + e.getMessage());
                }
            }

            return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Step 1: Generate OTP and send it to the user via email
    @PostMapping("/send-otp")
    public ResponseEntity<OtpResponse> sendOtp(@RequestBody EmailRequest userRequest) {
        try {
            OtpResponse result = userService.sendOtp(userRequest);

            switch (result.getStatus()) {
                case "SUCCESS":
                    return new ResponseEntity<>(result, HttpStatus.OK);
                case "ERROR":
                    String msg = result.getMessage().toLowerCase();
                    if (msg.contains("does not exist")) {
                        return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
                    } else if (msg.contains("invalid credentials")) {
                        return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
                    } else {
                        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
                    }
                default:
                    return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            OtpResponse errorResponse = new OtpResponse("ERROR", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Step 2: Verify OTP and log in the user if OTP is correct
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user, @RequestParam String otp) {
        try {
            LoginResponse jwt = userService.verifyOtpAndLogin(user, otp);
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/reset-password-request")
    public ResponseEntity<String> resetPasswordRequest(@RequestBody ResetPasswordRequest request) {
        try {
            userService.initiatePasswordReset(request);
            return ResponseEntity.ok("Password reset link has been sent to your email.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            userService.resetPassword(request);
            return ResponseEntity.ok("Successfully reset your password.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }

            Object principal = authentication.getPrincipal();
            String email = null;
            if (principal instanceof CustomUserDetails cud) {
                email = cud.getEmail();
            } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails ud) {
                // Fallback if another principal implementation is used
                email = ud.getUsername();
            } else if (principal instanceof String s) {
                email = s; // Sometimes principal might be a username string
            }

            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unable to resolve current user");
            }

            java.util.Optional<User> maybeUser = userService.findByEmail(email);
            if (maybeUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            return ResponseEntity.ok(toProfile(maybeUser.get()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
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
                .dateOfBirth(user.getDate_of_Birth())
                .position(user.getPosition())
                .build();
    }

}
