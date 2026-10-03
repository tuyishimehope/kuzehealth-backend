package rw.ac.auca.kuzahealth.controller.vaccination;

import java.util.Date;
import java.util.List;
import java.util.UUID;

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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.core.vaccination.dto.VaccinationRequest;
import rw.ac.auca.kuzahealth.core.vaccination.entity.Vaccination;
import rw.ac.auca.kuzahealth.core.vaccination.service.VaccinationService;
import rw.ac.auca.kuzahealth.utils.MessageResponse;
import rw.ac.auca.kuzahealth.utils.paging.PageRequests;
import rw.ac.auca.kuzahealth.utils.paging.PageResponse;





/**
 * REST controller for managing vaccinations
 */
@RestController
@RequestMapping({ "/api/vaccinations", "/api/v1/vaccinations" })
@RequiredArgsConstructor
public class VaccinationController {
    private final VaccinationService vaccinationService;

    /**
     * Record a vaccination and notify the infant's mother by SMS
     *
     * @param request the vaccination request
     * @return the created vaccination
     */
    @PostMapping
    public ResponseEntity<Vaccination> createVaccination(@RequestBody @Valid VaccinationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vaccinationService.createVaccination(request));
    }

    /**
     * Paged, filterable alternative to the full list
     */
    @GetMapping("/search")
    public PageResponse<Vaccination> search(@RequestParam(required = false) UUID infantId,
            @RequestParam(required = false) UUID healthWorkerId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort) {
        return PageResponse.of(vaccinationService.search(infantId, healthWorkerId, PageRequests.of(page, size, sort)));
    }

    /**
     * Get all vaccinations
     *
     * @return list of all vaccinations
     */
    @GetMapping
    public ResponseEntity<List<Vaccination>> getAllVaccinations() {
        List<Vaccination> vaccinations = vaccinationService.findAll();
        return new ResponseEntity<>(vaccinations, HttpStatus.OK);
    }

    /**
     * Get a vaccination by ID
     *
     * @param id the ID of the vaccination
     * @return the vaccination if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Vaccination> getVaccinationById(@PathVariable UUID id) {
        Vaccination vaccination = vaccinationService.findById(id);
        return new ResponseEntity<>(vaccination, HttpStatus.OK);
    }

    /**
     * Get all vaccinations for a specific infant
     *
     * @param infantId the ID of the infant
     * @return list of vaccinations for the infant
     */
    @GetMapping("/infant/{infantId}")
    public ResponseEntity<List<Vaccination>> getVaccinationsByInfantId(@PathVariable UUID infantId) {
        List<Vaccination> vaccinations = vaccinationService.findByInfantId(infantId);
        return new ResponseEntity<>(vaccinations, HttpStatus.OK);
    }

    /**
     * Get all vaccinations administered by a specific health worker
     *
     * @param healthWorkerId the ID of the health worker
     * @return list of vaccinations administered by the health worker
     */
    @GetMapping("/healthworker/{healthWorkerId}")
    public ResponseEntity<List<Vaccination>> getVaccinationsByHealthWorkerId(@PathVariable UUID healthWorkerId) {
        List<Vaccination> vaccinations = vaccinationService.findByHealthWorkerId(healthWorkerId);
        return new ResponseEntity<>(vaccinations, HttpStatus.OK);
    }

    /**
     * Get all vaccinations for infants of a specific parent
     *
     * @param parentId the ID of the parent
     * @return list of vaccinations for infants of the parent
     */
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<Vaccination>> getVaccinationsByParentId(@PathVariable UUID parentId) {
        List<Vaccination> vaccinations = vaccinationService.findByParentId(parentId);
        return new ResponseEntity<>(vaccinations, HttpStatus.OK);
    }

    /**
     * Get all due vaccinations
     *
     * @param date the date to check against (default is current date)
     * @return list of due vaccinations
     */
    @GetMapping("/due")
    public ResponseEntity<List<Vaccination>> getDueVaccinations(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date) {
        if (date == null) {
            date = new Date();
        }
        List<Vaccination> vaccinations = vaccinationService.findDueVaccinations(date);
        return new ResponseEntity<>(vaccinations, HttpStatus.OK);
    }

    /**
     * Send notifications for due vaccinations
     *
     * @param date the date to check against (default is current date)
     * @return message with number of notifications sent
     */
    @PostMapping("/notify")
    public ResponseEntity<MessageResponse> sendDueVaccinationNotifications(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date) {
        if (date == null) {
            date = new Date();
        }
        int notificationsSent = vaccinationService.sendDueVaccinationNotifications(date);
        return new ResponseEntity<>(
                new MessageResponse(notificationsSent + " vaccination notifications sent", HttpStatus.OK),
                HttpStatus.OK);
    }

    /**
     * Update a vaccination
     *
     * @param id      the ID of the vaccination to update
     * @param request the updated vaccination data
     * @return the updated vaccination
     */
    @PutMapping("/{id}")
    public ResponseEntity<Vaccination> updateVaccination(@PathVariable UUID id, @RequestBody @Valid VaccinationRequest request) {
        Vaccination vaccination = vaccinationService.updateVaccination(id, request);
        return new ResponseEntity<>(vaccination, HttpStatus.OK);
    }

    /**
     * Delete a vaccination
     *
     * @param id the ID of the vaccination to delete
     * @return success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteVaccination(@PathVariable UUID id) {
        vaccinationService.deleteById(id);
        return new ResponseEntity<>(
                new MessageResponse("Vaccination deleted successfully", HttpStatus.OK),
                HttpStatus.OK);
    }
}