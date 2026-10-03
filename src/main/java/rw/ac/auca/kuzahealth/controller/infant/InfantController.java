package rw.ac.auca.kuzahealth.controller.infant;

import java.util.List;
import java.util.UUID;

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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.core.infant.dto.InfantRequest;
import rw.ac.auca.kuzahealth.core.immunisation.ImmunisationService;
import rw.ac.auca.kuzahealth.core.immunisation.ScheduledDose;
import rw.ac.auca.kuzahealth.core.infant.entity.Infant;
import rw.ac.auca.kuzahealth.core.infant.service.InfantService;
import rw.ac.auca.kuzahealth.utils.MessageResponse;
import rw.ac.auca.kuzahealth.utils.paging.PageRequests;
import rw.ac.auca.kuzahealth.utils.paging.PageResponse;

@RestController
@RequestMapping({ "/api/infants", "/api/v1/infants" })
@RequiredArgsConstructor
public class InfantController {

    private final InfantService infantService;
    private final ImmunisationService immunisationService;

    @PostMapping
    public ResponseEntity<Infant> createInfant(@RequestBody @Valid InfantRequest request) {
        return new ResponseEntity<>(infantService.createInfant(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Infant>> getAllInfants() {
        List<Infant> infants = infantService.findAll();
        return new ResponseEntity<>(infants, HttpStatus.OK);
    }

    /** Paged, filterable alternative to the full list. */
    @GetMapping("/search")
    public PageResponse<Infant> search(@RequestParam(required = false) String q,
            @RequestParam(required = false) UUID motherId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort) {
        return PageResponse.of(infantService.search(q, motherId, PageRequests.of(page, size, sort)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Infant> getInfantById(@PathVariable UUID id) {
        Infant infant = infantService.findById(id);
        return new ResponseEntity<>(infant, HttpStatus.OK);
    }

    /** Each scheduled dose for this infant: given, upcoming, due or overdue. */
    @GetMapping("/{id}/immunisation-schedule")
    public List<ScheduledDose> immunisationSchedule(@PathVariable UUID id) {
        return immunisationService.scheduleFor(id);
    }

    @GetMapping("/mother/{motherId}")
    public ResponseEntity<List<Infant>> getInfantsByMotherId(@PathVariable UUID motherId) {
        List<Infant> infants = infantService.findByMotherId(motherId);
        return new ResponseEntity<>(infants, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Infant> updateInfant(@PathVariable UUID id, @RequestBody @Valid InfantRequest request) {
        return new ResponseEntity<>(infantService.updateInfant(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteInfant(@PathVariable UUID id) {
        infantService.deleteById(id);
        return new ResponseEntity<>(new MessageResponse("Infant deleted successfully", HttpStatus.OK), HttpStatus.OK);
    }
}
