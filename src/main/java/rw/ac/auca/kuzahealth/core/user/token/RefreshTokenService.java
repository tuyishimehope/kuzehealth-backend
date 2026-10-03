package rw.ac.auca.kuzahealth.core.user.token;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.HexFormat;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.core.exception.BadRequestException;
import rw.ac.auca.kuzahealth.core.user.entity.User;
import rw.ac.auca.kuzahealth.core.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String INVALID = "Invalid or expired refresh token";

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${app.auth.refresh-ttl-days:30}")
    private long refreshTtlDays;

    /** @return the raw token; it is shown to the client once and never stored */
    @Transactional
    public String issue(User user) {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setTokenHash(hash(raw));
        token.setExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(refreshTtlDays)));
        refreshTokenRepository.save(token);
        return raw;
    }

    /**
     * Consumes a refresh token (each one is single use) and returns its owner.
     * Presenting a token that was already used revokes every session of that user,
     * because it means the token was copied.
     */
    @Transactional(noRollbackFor = BadRequestException.class)
    public User consume(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new BadRequestException(INVALID);
        }
        RefreshToken token = refreshTokenRepository.findByTokenHash(hash(raw))
                .orElseThrow(() -> new BadRequestException(INVALID));
        User user = token.getUser();

        if (token.isRevoked()) {
            revokeAll(user);
            throw new BadRequestException(INVALID);
        }
        if (token.getExpiresAt().before(new Date()) || !user.isEnabled()) {
            throw new BadRequestException(INVALID);
        }
        token.setRevoked(true);
        refreshTokenRepository.save(token);
        return user;
    }

    /** Signs the user out everywhere: no refresh token and no access token issued so far stays valid. */
    @Transactional
    public void revokeAll(User user) {
        refreshTokenRepository.revokeAllForUser(user.getId());
        user.setTokensInvalidBefore(System.currentTimeMillis());
        userRepository.save(user);
    }

    @Transactional
    public int deleteExpired() {
        return refreshTokenRepository.deleteExpiredBefore(new Date());
    }

    private static String hash(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(raw.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
