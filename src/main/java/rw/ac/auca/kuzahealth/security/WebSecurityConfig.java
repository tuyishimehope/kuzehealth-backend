package rw.ac.auca.kuzahealth.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.utils.ApiError;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private static final String ADMIN = "ADMIN";
    private static final String HEALTH_WORKER = "HEALTH_WORKER";

    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/v1/auth/login", "/api/v1/auth/register", "/api/v1/auth/send-otp",
            "/api/v1/auth/reset-password-request", "/api/v1/auth/reset-password", "/api/v1/auth/refresh",
            "/api/v1/test/greet",
            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
            "/actuator/health/**", "/actuator/info"
    };

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final rw.ac.auca.kuzahealth.core.audit.AuditLoggingFilter auditLoggingFilter;
    private final ObjectMapper objectMapper;

    @Value("${app.rate-limit.auth-per-minute:10}")
    private int authLimitPerMinute;

    @Value("${app.rate-limit.sms-per-hour:60}")
    private int smsLimitPerHour;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        RateLimitFilter rateLimitFilter = new RateLimitFilter(authLimitPerMinute, smsLimitPerHour, objectMapper);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        // Operational and administrative surfaces
                        .requestMatchers("/actuator/**", "/api/logging/**", "/api/v1/logging/**", "/api/v1/audit/**").hasRole(ADMIN)
                        // Accounts: finer rules (self or admin) are enforced on the controller methods
                        .requestMatchers("/api/v1/auth/**", "/api/v1/test/**", "/api/users/**", "/api/v1/users/**")
                        .authenticated()
                        // Staff records are managed by admins and readable by any signed-in user
                        .requestMatchers(HttpMethod.GET, "/api/health-workers/**", "/api/v1/health-workers/**")
                        .authenticated()
                        .requestMatchers("/api/health-workers/**", "/api/v1/health-workers/**").hasRole(ADMIN)
                        // Clinical data: everyone signed in may read, only admins and health workers may write
                        .requestMatchers(HttpMethod.GET, "/api/**").authenticated()
                        .requestMatchers("/api/**").hasAnyRole(ADMIN, HEALTH_WORKER)
                        .anyRequest().authenticated())
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint((request, response, ex) -> writeError(response,
                                HttpStatus.UNAUTHORIZED, "Authentication is required."))
                        .accessDeniedHandler((request, response, ex) -> writeError(response,
                                HttpStatus.FORBIDDEN, "You do not have permission to do this.")))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(auditLoggingFilter, RateLimitFilter.class);

        return http.build();
    }

    private void writeError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), ApiError.of(status, status.name(), message));
    }
}
