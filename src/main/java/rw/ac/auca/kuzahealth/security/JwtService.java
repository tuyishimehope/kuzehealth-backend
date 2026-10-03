package rw.ac.auca.kuzahealth.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import rw.ac.auca.kuzahealth.core.user.entity.User;

@Service
public class JwtService {

    private static final int MIN_KEY_BYTES = 32;
    private static final String ISSUED_AT_MILLIS = "iatMs";

    private final Key signInKey;
    private final long expirationMillis;

    public JwtService(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long expirationSeconds) {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (RuntimeException e) {
            throw new IllegalStateException("JWT_SECRET must be a base64 string", e);
        }
        if (keyBytes.length < MIN_KEY_BYTES) {
            throw new IllegalStateException("JWT_SECRET must decode to at least " + MIN_KEY_BYTES + " bytes");
        }
        this.signInKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMillis = expirationSeconds * 1000;
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());

        long now = System.currentTimeMillis();
        claims.put(ISSUED_AT_MILLIS, now);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername() != null ? user.getUsername() : user.getEmail())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationMillis))
                .signWith(signInKey)
                .compact();
    }

    /**
     * Verifies the signature and expiry, and returns the claims.
     *
     * @throws io.jsonwebtoken.JwtException if the token is invalid or expired
     */
    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signInKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /** When the token was issued, in epoch millis. */
    public static long issuedAtMillis(Claims claims) {
        Long precise = claims.get(ISSUED_AT_MILLIS, Long.class);
        if (precise != null) {
            return precise;
        }
        return claims.getIssuedAt() != null ? claims.getIssuedAt().getTime() : 0L;
    }

    public long getExpirationSeconds() {
        return expirationMillis / 1000;
    }
}
