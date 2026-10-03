package rw.ac.auca.kuzahealth.controller.anc;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.core.anc.AncPlan;
import rw.ac.auca.kuzahealth.core.anc.AncScheduleRequest;
import rw.ac.auca.kuzahealth.core.anc.AncService;
import rw.ac.auca.kuzahealth.core.visit.entity.Visit;

@RestController
@RequestMapping({ "/api/pregnancy-records", "/api/v1/pregnancy-records" })
@RequiredArgsConstructor
public class AncController {

    private final AncService ancService;

    /** Expected delivery date, gestational age, risk flags and the antenatal contact schedule. */
    @GetMapping("/{id}/anc-plan")
    public AncPlan plan(@PathVariable UUID id) {
        return ancService.plan(id);
    }

    /** Books a visit for each remaining antenatal contact that has none. */
    @PostMapping("/{id}/anc-plan/schedule")
    public ResponseEntity<List<Visit>> schedule(@PathVariable UUID id, @RequestBody @Valid AncScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ancService.scheduleRemaining(id, request));
    }

    /** Active pregnancies with at least one risk flag. */
    @GetMapping("/high-risk")
    public List<AncPlan> highRisk() {
        return ancService.highRisk();
    }
}
