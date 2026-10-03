package rw.ac.auca.kuzahealth.controller.immunisation;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import rw.ac.auca.kuzahealth.core.immunisation.ImmunisationService;
import rw.ac.auca.kuzahealth.core.immunisation.OverdueInfant;
import rw.ac.auca.kuzahealth.core.immunisation.ScheduledDose;
import rw.ac.auca.kuzahealth.core.immunisation.VaccineScheduleItem;
import rw.ac.auca.kuzahealth.core.immunisation.VaccineScheduleItemRequest;

@RestController
@RequestMapping("/api/v1/immunisation")
@RequiredArgsConstructor
public class ImmunisationController {

    private final ImmunisationService immunisationService;

    /** The routine schedule: every dose and the age at which it is due. */
    @GetMapping("/schedule")
    public List<VaccineScheduleItem> schedule() {
        return immunisationService.getSchedule();
    }

    @PostMapping("/schedule")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VaccineScheduleItem> createItem(@RequestBody @Valid VaccineScheduleItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(immunisationService.createItem(request));
    }

    @PutMapping("/schedule/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public VaccineScheduleItem updateItem(@PathVariable UUID id,
            @RequestBody @Valid VaccineScheduleItemRequest request) {
        return immunisationService.updateItem(id, request);
    }

    @DeleteMapping("/schedule/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateItem(@PathVariable UUID id) {
        immunisationService.deactivateItem(id);
        return ResponseEntity.noContent().build();
    }

    /** Each scheduled dose for one infant: given, upcoming, due or overdue. */
    @GetMapping("/infants/{infantId}")
    public List<ScheduledDose> forInfant(@PathVariable UUID infantId) {
        return immunisationService.scheduleFor(infantId);
    }

    /** Infants with overdue doses and how to reach their mothers. */
    @GetMapping("/overdue")
    public List<OverdueInfant> overdue(@RequestParam(required = false) String district) {
        return immunisationService.overdue(district);
    }
}
