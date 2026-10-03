package rw.ac.auca.kuzahealth.controller.healthworker;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.core.caseload.CaseloadSummary;
import rw.ac.auca.kuzahealth.core.healthworker.dto.HealthWorkerRequest;
import rw.ac.auca.kuzahealth.core.healthworker.entity.HealthWorker;
import rw.ac.auca.kuzahealth.core.healthworker.service.HealthWorkerService;
import rw.ac.auca.kuzahealth.security.CustomUserDetails;
import rw.ac.auca.kuzahealth.utils.paging.PageRequests;
import rw.ac.auca.kuzahealth.utils.paging.PageResponse;

@RestController
@RequestMapping({ "/api/health-workers", "/api/v1/health-workers" })
@RequiredArgsConstructor
public class HealthWorkerController {

    private final HealthWorkerService healthWorkerService;

    @Value("${app.reminders.missed-after-hours:24}")
    private long missedAfterHours;

    @PostMapping
    public ResponseEntity<HealthWorker> createHealthWorker(@RequestBody @Valid HealthWorkerRequest healthWorker) {
        return new ResponseEntity<>(healthWorkerService.createHealthWorker(healthWorker), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<HealthWorker>> getHealthWorkers() {
        return ResponseEntity.ok(healthWorkerService.getAllHealthWorkers());
    }

    @GetMapping("/search")
    public PageResponse<HealthWorker> search(@RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort) {
        return PageResponse.of(healthWorkerService.search(q, PageRequests.of(page, size, sort)));
    }

    /** The staff record of the signed-in user. */
    @GetMapping("/me")
    public HealthWorker me(@AuthenticationPrincipal CustomUserDetails caller) {
        return healthWorkerService.getForUser(caller.getId(), caller.getEmail());
    }

    /** Caseload of the signed-in health worker. */
    @GetMapping("/me/caseload")
    public CaseloadSummary myCaseload(@AuthenticationPrincipal CustomUserDetails caller) {
        return healthWorkerService.caseload(me(caller).getId(), missedAfterHours);
    }

    /** Size of a health worker's caseload. The parents themselves are at /parents/search?assignedHealthWorkerId=. */
    @GetMapping("/{id}/caseload")
    public CaseloadSummary caseload(@PathVariable("id") UUID id) {
        return healthWorkerService.caseload(id, missedAfterHours);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HealthWorker> updateHealthWorker(@PathVariable("id") UUID id,
            @RequestBody @Valid HealthWorkerRequest healthWorker) {
        return ResponseEntity.ok(healthWorkerService.updateHealthWorker(id, healthWorker));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHealthWorker(@PathVariable("id") UUID id) {
        healthWorkerService.deleteHealthWorker(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<HealthWorker> getHealthWorkerById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(healthWorkerService.getHealthWorkerById(id));
    }
}
