package rw.ac.auca.kuzahealth.controller.auth.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    @GetMapping
    public ResponseEntity<Map<String, String>> testAuth(Authentication authentication) {
        return ResponseEntity.ok(Map.of(
                "message", "Authenticated successfully",
                "user", authentication.getName(),
                "roles", authentication.getAuthorities().toString()
        ));
    }

    @GetMapping("/greet")
    public String greet() {
        return "Welcome";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String admin() {
        return "Hello Admin!";
    }
}
