package rw.ac.auca.kuzahealth.controller.audit;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.core.audit.entity.AuditLog;
import rw.ac.auca.kuzahealth.core.audit.repository.AuditLogRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/audit")
public class AuditLogController {

    private final AuditLogRepository repository;

    @GetMapping("/logs")
    public ResponseEntity<List<AuditLog>> list(
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String action,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date to
    ) {
        List<AuditLog> logs = repository.search(email, action, from, to);
        return ResponseEntity.ok(logs);
    }
}
