package rw.ac.auca.kuzahealth.core.audit;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.core.audit.entity.AuditLog;
import rw.ac.auca.kuzahealth.core.audit.repository.AuditLogRepository;

@Component
@RequiredArgsConstructor
public class AuditLoggingFilter extends OncePerRequestFilter {

    private final AuditLogRepository auditLogRepository;

    private static final Pattern UUID_PATTERN = Pattern.compile(
        "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String method = request.getMethod();
        boolean isWrite = "POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method) || "DELETE".equals(method);

        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();

        // Proceed with the chain first to ensure business logic executes
        try {
            filterChain.doFilter(request, response);
        } finally {
            if (isWrite && uri.startsWith("/api/")) {
                String username = null;
                String email = null;
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.getPrincipal() instanceof UserDetails userDetails) {
                    username = userDetails.getUsername();
                    // Try to extract email if CustomUserDetails exposes it via username or authorities are not used
                    // Many apps use email as username; we will set both for safety
                    email = userDetails.getUsername();
                }

                String entityId = extractIdFromUri(uri);

                AuditLog log = new AuditLog(method, uri, entityId, username, email, ip);
                try {
                    auditLogRepository.save(log);
                } catch (Exception e) {
                    // Avoid breaking request flow on audit failure
                    // Optionally, add real logger here
                    e.printStackTrace();
                }
            }
        }
    }

    private String extractIdFromUri(String uri) {
        // naive approach: last path segment or UUID within uri
        if (uri == null) return null;
        Matcher m = UUID_PATTERN.matcher(uri);
        if (m.find()) return m.group();
        int idx = uri.lastIndexOf('/');
        if (idx > 0 && idx < uri.length() - 1) {
            String last = uri.substring(idx + 1);
            // Only consider last segment if it's not obviously a resource name
            if (last.matches("[A-Za-z0-9_-]{1,64}")) {
                return last;
            }
        }
        return null;
    }
}
