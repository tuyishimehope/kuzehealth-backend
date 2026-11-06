package rw.ac.auca.kuzahealth.core.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.controller.auth.dto.LoginRequest;
import rw.ac.auca.kuzahealth.controller.auth.dto.LoginResponse;
import rw.ac.auca.kuzahealth.controller.auth.dto.RegisterRequest;
import rw.ac.auca.kuzahealth.controller.auth.dto.ResetPasswordRequest;
import rw.ac.auca.kuzahealth.controller.user.dto.UpdateUserRequest;
import rw.ac.auca.kuzahealth.core.exception.BadRequestException;
import rw.ac.auca.kuzahealth.core.exception.DuplicateResourceException;
import rw.ac.auca.kuzahealth.core.exception.ResourceNotFoundException;
import rw.ac.auca.kuzahealth.core.exception.TooManyRequestsException;
import rw.ac.auca.kuzahealth.core.exception.UnauthorizedException;
import rw.ac.auca.kuzahealth.core.user.entity.User;
import rw.ac.auca.kuzahealth.core.user.enums.EUserType;
import rw.ac.auca.kuzahealth.core.user.repository.UserRepository;
import rw.ac.auca.kuzahealth.core.user.token.RefreshTokenService;
import rw.ac.auca.kuzahealth.security.JwtService;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final String INVALID_CREDENTIALS = "Invalid credentials. Please try again.";

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthMailService authMailService;
    private final LoginAttemptService loginAttemptService;
    private final RefreshTokenService refreshTokenService;

    @Value("${app.auth.reset-ttl-minutes:15}")
    private long resetTtlMinutes;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User registerUser(RegisterRequest request, EUserType role) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User with this email already exists.");
        }
        if (request.getUsername() != null && userRepository.findByUsername(request.getUsername()) != null) {
            throw new DuplicateResourceException("This username is already taken.");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setUsername(request.getUsername());
        user.setProvince(request.getProvince());
        user.setDistrict(request.getDistrict());
        user.setSector(request.getSector());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setPosition(request.getPosition());
        user.setGender(request.getGender());
        user.setRole(role);

        User savedUser = userRepository.save(user);
        logger.info("Registered user {} with role {}", savedUser.getId(), savedUser.getRole());
        return savedUser;
    }

    /**
     * Signs in with an email address or phone number and a password, and issues tokens.
     * Unknown accounts, disabled accounts and wrong passwords get the same answer so
     * that this endpoint cannot be used to discover which accounts exist.
     */
    public LoginResponse login(LoginRequest request) {
        String identifier = request.getEmail();
        if (loginAttemptService.isLocked(identifier)) {
            throw new TooManyRequestsException("Too many failed attempts. Please try again later.");
        }

        User user = findByIdentifier(identifier)
                .filter(User::isEnabled)
                .filter(candidate -> passwordEncoder.matches(request.getPassword(), candidate.getPassword()))
                .orElse(null);
        if (user == null) {
            loginAttemptService.recordFailure(identifier);
            throw new UnauthorizedException(INVALID_CREDENTIALS);
        }

        loginAttemptService.recordSuccess(identifier);
        return tokensFor(user, "Login successful");
    }

    /** Exchanges a refresh token for a new access token and a new refresh token. */
    public LoginResponse refresh(String refreshToken) {
        return tokensFor(refreshTokenService.consume(refreshToken), "Token refreshed");
    }

    /** Ends every session of the user: refresh tokens are revoked and issued access tokens stop working. */
    public void logout(UUID userId) {
        refreshTokenService.revokeAll(getUserById(userId));
    }

    private LoginResponse tokensFor(User user, String message) {
        return LoginResponse.builder()
                .token(jwtService.generateToken(user))
                .refreshToken(refreshTokenService.issue(user))
                .expiresIn(jwtService.getExpirationSeconds())
                .email(user.getEmail())
                .userType(user.getRole() != null ? user.getRole().name() : null)
                .message(message)
                .build();
    }

    /**
     * Starts a password reset. Does nothing for unknown addresses; the caller gets the
     * same response either way.
     */
    public void initiatePasswordReset(ResetPasswordRequest request) {
        Optional<User> found = userRepository.findByEmail(request.getEmail());
        if (found.isEmpty()) {
            return;
        }
        User user = found.get();
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiration(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(resetTtlMinutes));
        userRepository.save(user);

        authMailService.sendPasswordReset(user.getEmail(), token, resetTtlMinutes);
    }

    public void resetPassword(ResetPasswordRequest request) {
        String newPassword = request.getConfirmPassword() != null ? request.getConfirmPassword()
                : request.getPassword();
        if (newPassword == null || newPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new BadRequestException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters.");
        }
        if (request.getPassword() != null && request.getConfirmPassword() != null
                && !request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match.");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid or expired reset token"));

        if (request.getToken() == null || user.getResetToken() == null
                || !user.getResetToken().equals(request.getToken())
                || user.getResetTokenExpiration() == null
                || System.currentTimeMillis() > user.getResetTokenExpiration()) {
            throw new BadRequestException("Invalid or expired reset token");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiration(null);
        userRepository.save(user);
        refreshTokenService.revokeAll(user);
        logger.info("Password reset for user {}", user.getId());
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Page<User> search(String q, EUserType role, Pageable pageable) {
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (q != null && !q.isBlank()) {
                String like = "%" + q.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("firstName")), like),
                        cb.like(cb.lower(root.get("lastName")), like),
                        cb.like(cb.lower(root.get("email")), like)));
            }
            if (role != null) {
                predicates.add(cb.equal(root.get("role"), role));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
        return userRepository.findAll(spec, pageable);
    }

    /**
     * @param byAdmin whether the caller may change the role and the enabled flag
     */
    public User updateUser(UUID id, UpdateUserRequest request, boolean byAdmin) {
        User user = getUserById(id);

        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("User with this email already exists.");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getProvince() != null) {
            user.setProvince(request.getProvince());
        }
        if (request.getDistrict() != null) {
            user.setDistrict(request.getDistrict());
        }
        if (request.getSector() != null) {
            user.setSector(request.getSector());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getPosition() != null) {
            user.setPosition(request.getPosition());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (byAdmin) {
            if (request.getRole() != null) {
                user.setRole(request.getRole());
            }
            if (request.getEnabled() != null) {
                user.setEnabled(request.getEnabled());
            }
            if (Boolean.FALSE.equals(request.getEnabled())) {
                refreshTokenService.revokeAll(user);
            }
        }

        return userRepository.save(user);
    }

    public void deleteUser(UUID id) {
        userRepository.delete(getUserById(id));
        logger.info("Deleted user {}", id);
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Optional<User> findByIdentifier(String identifier) {
        if (identifier == null) {
            return Optional.empty();
        }
        return identifier.contains("@")
                ? userRepository.findByEmail(identifier)
                : userRepository.findByPhoneNumber(identifier);
    }
}
