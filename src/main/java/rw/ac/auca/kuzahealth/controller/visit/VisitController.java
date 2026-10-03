package rw.ac.auca.kuzahealth.controller.visit;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.core.visit.dto.VisitRequest;
import rw.ac.auca.kuzahealth.core.visit.entity.Visit;
import rw.ac.auca.kuzahealth.core.visit.enums.VisitStatus;
import rw.ac.auca.kuzahealth.core.visit.service.VisitService;
import rw.ac.auca.kuzahealth.utils.paging.PageRequests;
import rw.ac.auca.kuzahealth.utils.paging.PageResponse;

@RestController
@RequestMapping({ "/api/visits", "/api/v1/visits" })
@RequiredArgsConstructor
public class VisitController {

    private final VisitService visitService;

    @Value("${app.reminders.missed-after-hours:24}")
    private long missedAfterHours;

    @PostMapping
    public ResponseEntity<Visit> createVisit(@RequestBody VisitRequest visitRequest) {
        Visit createdVisit = visitService.createVisit(visitRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVisit);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Visit> getVisitById(@PathVariable UUID id) {
        return visitService.getVisitById(id)
                .map(visit -> new ResponseEntity<>(visit, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<Visit>> getAllVisits() {
        List<Visit> visits = visitService.getAllVisits();
        return new ResponseEntity<>(visits, HttpStatus.OK);
    }

    /** Paged, filterable alternative to the full list. */
    @GetMapping("/search")
    public PageResponse<Visit> search(@RequestParam(required = false) String status,
            @RequestParam(required = false) UUID healthWorkerId,
            @RequestParam(required = false) UUID parentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date to,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort) {
        return PageResponse.of(visitService.search(VisitStatus.parse(status), healthWorkerId, parentId, from, to,
                PageRequests.of(page, size, sort)));
    }

    /** Scheduled visits in the coming days, soonest first. */
    @GetMapping("/upcoming")
    public List<Visit> upcoming(@RequestParam(required = false) UUID healthWorkerId,
            @RequestParam(defaultValue = "7") int days) {
        return visitService.upcoming(healthWorkerId, Math.min(days, 90));
    }

    /** Visits the parent did not attend, for follow-up. */
    @GetMapping("/missed")
    public List<Visit> missed(@RequestParam(required = false) UUID healthWorkerId) {
        return visitService.missed(healthWorkerId, missedAfterHours);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Visit>> getAllVisitsByPatientId(@PathVariable UUID patientId) {
        return new ResponseEntity<>(visitService.getVisitByParentId(patientId), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE, produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Visit> updateVisit(@PathVariable UUID id, @RequestBody VisitRequest visitRequest) {
        Visit updatedVisit = visitService.updateVisit(id, visitRequest);
        return new ResponseEntity<>(updatedVisit, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVisit(@PathVariable UUID id) {
        visitService.deleteVisit(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
