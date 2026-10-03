package rw.ac.auca.kuzahealth.controller.audit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.core.audit.entity.AuditLog;
import rw.ac.auca.kuzahealth.core.audit.repository.AuditLogRepository;
import rw.ac.auca.kuzahealth.utils.paging.PageRequests;
import rw.ac.auca.kuzahealth.utils.paging.PageResponse;

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

    @GetMapping("/logs/search")
    public PageResponse<AuditLog> search(
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String action,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date to,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "50") int size
    ) {
        Specification<AuditLog> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (email != null) predicates.add(cb.equal(root.get("email"), email));
            if (action != null) predicates.add(cb.equal(root.get("action"), action.toUpperCase()));
            if (from != null) predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), from));
            if (to != null) predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), to));
            return cb.and(predicates.toArray(Predicate[]::new));
        };
        return PageResponse.of(repository.findAll(spec, PageRequests.of(page, size, null)));
    }
}
