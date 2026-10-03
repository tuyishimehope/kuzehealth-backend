package rw.ac.auca.kuzahealth.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import rw.ac.auca.kuzahealth.utils.ApiError;

/**
 * Fixed-window rate limiting for the endpoints that are cheap to abuse: credential
 * endpoints (per client IP) and SMS sending (per user). Counters live in memory, so
 * the limits apply per application instance.
 */
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Set<String> AUTH_ENDPOINTS = Set.of(
            "/api/v1/auth/login", "/api/v1/auth/send-otp", "/api/v1/auth/register",
            "/api/v1/auth/reset-password-request", "/api/v1/auth/reset-password", "/api/v1/auth/refresh");
    private static final long AUTH_WINDOW_MILLIS = 60_000;
    private static final long SMS_WINDOW_MILLIS = 3_600_000;
    private static final int MAX_TRACKED_KEYS = 20_000;

    private final int authLimitPerMinute;
    private final int smsLimitPerHour;
    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    public RateLimitFilter(int authLimitPerMinute, int smsLimitPerHour, ObjectMapper objectMapper) {
        this.authLimitPerMinute = authLimitPerMinute;
        this.smsLimitPerHour = smsLimitPerHour;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if ("POST".equals(request.getMethod())) {
            String path = request.getRequestURI();
            boolean allowed = true;
            if (AUTH_ENDPOINTS.contains(path)) {
                allowed = tryAcquire("auth:" + path + ":" + request.getRemoteAddr(), authLimitPerMinute,
                        AUTH_WINDOW_MILLIS);
            } else if (isSmsEndpoint(path)) {
                allowed = tryAcquire("sms:" + currentCaller(request), smsLimitPerHour, SMS_WINDOW_MILLIS);
            }
            if (!allowed) {
                reject(response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private static boolean isSmsEndpoint(String path) {
        return path.startsWith("/api/sms/") || path.startsWith("/api/v1/sms/")
                || path.equals("/api/nutrition-info") || path.equals("/api/v1/nutrition-info");
    }

    private static String currentCaller(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails user) {
            return user.getEmail();
        }
        return request.getRemoteAddr();
    }

    private boolean tryAcquire(String key, int limit, long windowMillis) {
        long now = System.currentTimeMillis();
        if (windows.size() > MAX_TRACKED_KEYS) {
            windows.values().removeIf(window -> window.expiresAt <= now);
        }
        Window window = windows.compute(key, (k, existing) -> {
            if (existing == null || existing.expiresAt <= now) {
                return new Window(now + windowMillis, 1);
            }
            return new Window(existing.expiresAt, existing.count + 1);
        });
        return window.count <= limit;
    }

    private void reject(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), new ApiError("TOO_MANY_REQUESTS",
                "Too many requests. Please wait and try again.", LocalDateTime.now(),
                HttpStatus.TOO_MANY_REQUESTS.value(), null));
    }

    private record Window(long expiresAt, int count) {
    }
}
