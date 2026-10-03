package rw.ac.auca.kuzahealth.core.user.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Temporarily locks an account identifier after repeated wrong passwords, so a
 * password cannot be guessed by spreading requests across many IP addresses.
 * State is in memory and therefore per application instance.
 */
@Service
public class LoginAttemptService {

    private final int maxFailures;
    private final long lockMillis;
    private final ConcurrentHashMap<String, Attempts> attempts = new ConcurrentHashMap<>();

    public LoginAttemptService(@Value("${app.auth.max-password-failures:10}") int maxFailures,
            @Value("${app.auth.lock-minutes:15}") long lockMinutes) {
        this.maxFailures = maxFailures;
        this.lockMillis = lockMinutes * 60_000;
    }

    public boolean isLocked(String identifier) {
        Attempts current = attempts.get(key(identifier));
        if (current == null) {
            return false;
        }
        if (current.expiresAt <= System.currentTimeMillis()) {
            attempts.remove(key(identifier));
            return false;
        }
        return current.failures >= maxFailures;
    }

    public void recordFailure(String identifier) {
        long now = System.currentTimeMillis();
        attempts.compute(key(identifier), (k, existing) -> existing == null || existing.expiresAt <= now
                ? new Attempts(1, now + lockMillis)
                : new Attempts(existing.failures + 1, existing.expiresAt));
    }

    public void recordSuccess(String identifier) {
        attempts.remove(key(identifier));
    }

    private static String key(String identifier) {
        return identifier == null ? "" : identifier.trim().toLowerCase();
    }

    private record Attempts(int failures, long expiresAt) {
    }
}
