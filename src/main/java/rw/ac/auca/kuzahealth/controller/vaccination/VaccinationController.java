package rw.ac.auca.kuzahealth.controller.vaccination;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.server.ResponseStatusException;
import rw.ac.auca.kuzahealth.core.infant.entity.Infant;
import rw.ac.auca.kuzahealth.sms.service.PindoSmsService;

import rw.ac.auca.kuzahealth.core.infant.repository.InfantRepository;
import rw.ac.auca.kuzahealth.core.parent.entity.Parent;
import rw.ac.auca.kuzahealth.core.parent.repository.ParentRepository;
import rw.ac.auca.kuzahealth.core.vaccination.dto.VaccinationRequest;
import rw.ac.auca.kuzahealth.core.vaccination.entity.Vaccination;
import rw.ac.auca.kuzahealth.core.vaccination.service.VaccinationService;
import rw.ac.auca.kuzahealth.sms.model.SmsRequest;
import rw.ac.auca.kuzahealth.sms.service.PindoSmsService;
import rw.ac.auca.kuzahealth.utils.MessageResponse;

import javax.swing.text.html.Option;

/**
 * REST controller for managing vaccinations
 */
@RestController
@RequestMapping("/api/vaccinations")
public class VaccinationController {
    private final VaccinationService vaccinationService;
    private final ParentRepository parentRepository;
    private final PindoSmsService smsService;
    private final InfantRepository infantRepository;
    private final Logger log = org.slf4j.LoggerFactory.getLogger(VaccinationController.class);

    @Autowired
    public VaccinationController(VaccinationService vaccinationService, ParentRepository parentRepository, PindoSmsService smsService,InfantRepository infantRepository) {
        this.vaccinationService = vaccinationService;
        this.parentRepository = parentRepository;
        this.smsService = smsService;
        this.infantRepository = infantRepository;
    }

    /**
     * Create a new vaccination
     *
     * @param request the vaccination request
     * @return the created vaccination
     */
    @PostMapping
    public ResponseEntity<Vaccination> createVaccination(@RequestBody VaccinationRequest request) {

        log.info("Creating vaccination for infant: {}", request.getName());
//        Optional<Infant> infantInfo = Optional.ofNullable(infantRepository.findById(request.getInfantId())
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Infant not found")));
        Infant infant = infantRepository.findById(request.getInfantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Infant not found"));

        Parent parentEntity = parentRepository.findById(infant.getMotherId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent not found"));
        log.info("Parent found: {}", parentEntity.getFirstName());

        // Create the vaccination record
        Vaccination vaccination = vaccinationService.createVaccination(request);

        // Send SMS notification
        sendVaccinationNotification(parentEntity, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(vaccination);
    }



    private void sendVaccinationNotification(Parent parent, VaccinationRequest request) {
        if (parent.getPhone() == null || parent.getPhone().isEmpty()) {
            log.warn("Parent {} has no phone number, skipping SMS", parent.getId());
            return;
        }

        String infantName = request.getName();
        String vaccinationDate = request.getAdministeredDate() != null
                ? request.getAdministeredDate().toString()
                : "TBD";

        String message = String.format(
                "Vaccination scheduled for %s on %s. Please ensure to bring the infant for vaccination.",
                infantName,
                vaccinationDate
        );

        smsService.sendSingleSms(parent.getPhone(), message, "PindoTest");
        log.info("SMS sent to {}: {}", parent.getPhone(), message);
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
    public ResponseEntity<Vaccination> updateVaccination(@PathVariable UUID id, @RequestBody VaccinationRequest request) {
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