package rw.ac.auca.kuzahealth.controller.growth;

import java.util.UUID;

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
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.core.growth.GrowthAssessment;
import rw.ac.auca.kuzahealth.core.growth.GrowthHistory;
import rw.ac.auca.kuzahealth.core.growth.GrowthMeasurementRequest;
import rw.ac.auca.kuzahealth.core.growth.GrowthService;
import rw.ac.auca.kuzahealth.security.CustomUserDetails;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class GrowthController {

    private final GrowthService growthService;

    /** Records weight, length, head circumference and/or MUAC for an infant. */
    @PostMapping("/infants/{infantId}/growth")
    public ResponseEntity<GrowthAssessment> record(@PathVariable UUID infantId,
            @RequestBody @Valid GrowthMeasurementRequest request,
            @AuthenticationPrincipal CustomUserDetails caller) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(growthService.record(infantId, request, caller.getEmail()));
    }

    /** The infant's growth history, oldest first, with z-scores and screening flags. */
    @GetMapping("/infants/{infantId}/growth")
    public GrowthHistory history(@PathVariable UUID infantId) {
        return growthService.history(infantId);
    }

    @PutMapping("/growth/{id}")
    public GrowthAssessment update(@PathVariable UUID id, @RequestBody @Valid GrowthMeasurementRequest request) {
        return growthService.update(id, request);
    }

    @DeleteMapping("/growth/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        growthService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
