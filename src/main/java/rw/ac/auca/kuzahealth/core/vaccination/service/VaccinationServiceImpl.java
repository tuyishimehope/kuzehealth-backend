package rw.ac.auca.kuzahealth.core.vaccination.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.core.exception.ResourceNotFoundException;
import rw.ac.auca.kuzahealth.core.caseload.CaseloadGuard;
import rw.ac.auca.kuzahealth.core.exception.BadRequestException;
import rw.ac.auca.kuzahealth.core.immunisation.ImmunisationService;
import rw.ac.auca.kuzahealth.core.immunisation.VaccineScheduleItem;
import rw.ac.auca.kuzahealth.core.healthworker.entity.HealthWorker;
import rw.ac.auca.kuzahealth.core.healthworker.repository.HealthWorkerRepository;
import rw.ac.auca.kuzahealth.core.infant.entity.Infant;
import rw.ac.auca.kuzahealth.core.infant.repository.InfantRepository;
import rw.ac.auca.kuzahealth.core.parent.entity.Parent;
import rw.ac.auca.kuzahealth.core.vaccination.dto.VaccinationRequest;
import rw.ac.auca.kuzahealth.core.vaccination.entity.Vaccination;
import rw.ac.auca.kuzahealth.core.vaccination.repository.VaccinationRepository;
import rw.ac.auca.kuzahealth.core.notification.NotificationService;
import rw.ac.auca.kuzahealth.core.notification.SmsPurpose;
import rw.ac.auca.kuzahealth.utils.MailService;
import rw.ac.auca.kuzahealth.utils.Dates;
import rw.ac.auca.kuzahealth.utils.SoftDeleter;

/**
 * Implementation of the VaccinationService interface
 */
@Service
@RequiredArgsConstructor
public class VaccinationServiceImpl implements VaccinationService {

    private static final Logger logger = LoggerFactory.getLogger(VaccinationServiceImpl.class);

    private final VaccinationRepository vaccinationRepository;
    private final InfantRepository infantRepository;
    private final HealthWorkerRepository healthWorkerRepository;
    private final MailService mailService;
    private final NotificationService notificationService;
    private final SoftDeleter softDeleter;
    private final CaseloadGuard caseloadGuard;
    private final ImmunisationService immunisationService;

    @Override
    @Transactional
    public Vaccination createVaccination(VaccinationRequest request) {
        Vaccination vaccination = new Vaccination();
        apply(request, vaccination);
        Vaccination saved = vaccinationRepository.save(vaccination);

        notificationService.notifyParent(saved.getInfant().getMother(), SmsPurpose.VACCINATION_RECORDED,
                "vaccination.recorded", saved.getName(), infantName(saved.getInfant()),
                notificationService.formatDate(saved.getAdministeredDate()));
        return saved;
    }

    @Override
    @Transactional
    public Vaccination updateVaccination(UUID id, VaccinationRequest request) {
        Vaccination vaccination = findById(id);
        apply(request, vaccination);
        return vaccinationRepository.save(vaccination);
    }

    @Override
    @Transactional(readOnly = true)
    public Vaccination findById(UUID id) {
        Vaccination vaccination = vaccinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vaccination not found with id: " + id));
        caseloadGuard.check(vaccination.getInfant().getMother());
        return vaccination;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vaccination> findAll() {
        return inCaseload(vaccinationRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Vaccination> search(UUID infantId, UUID healthWorkerId, Pageable pageable) {
        Optional<UUID> restrictedTo = caseloadGuard.restrictedTo();
        Specification<Vaccination> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            restrictedTo.ifPresent(workerId -> predicates.add(
                    cb.equal(root.get("infant").get("mother").get("assignedHealthWorker").get("id"), workerId)));
            if (infantId != null) {
                predicates.add(cb.equal(root.get("infant").get("id"), infantId));
            }
            if (healthWorkerId != null) {
                predicates.add(cb.equal(root.get("healthWorker").get("id"), healthWorkerId));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
        return vaccinationRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vaccination> findByInfant(Infant infant) {
        return inCaseload(vaccinationRepository.findByInfant(infant));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vaccination> findByInfantId(UUID infantId) {
        return inCaseload(vaccinationRepository.findByInfant_Id(infantId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vaccination> findByHealthWorker(HealthWorker healthWorker) {
        return inCaseload(vaccinationRepository.findByHealthWorker(healthWorker));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vaccination> findByHealthWorkerId(UUID healthWorkerId) {
        return inCaseload(vaccinationRepository.findByHealthWorker_Id(healthWorkerId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vaccination> findByParentId(UUID parentId) {
        return inCaseload(vaccinationRepository.findByParentId(parentId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vaccination> findDueVaccinations(Date date) {
        return inCaseload(vaccinationRepository.findByNextDueDateLessThanEqualAndNotificationSentFalse(date));
    }

    @Override
    @Transactional
    public int sendDueVaccinationNotifications(Date date) {
        int notificationsSent = 0;

        for (Vaccination vaccination : findDueVaccinations(date)) {
            if (remind(vaccination)) {
                notificationsSent++;
            }
        }

        return notificationsSent;
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        softDeleter.delete(Vaccination.class, findById(id).getId());
    }

    private void apply(VaccinationRequest request, Vaccination vaccination) {
        Infant infant = infantRepository.findById(request.getInfantId())
                .orElseThrow(() -> new ResourceNotFoundException("Infant not found with id: " + request.getInfantId()));
        caseloadGuard.check(infant.getMother());
        HealthWorker healthWorker = healthWorkerRepository.findById(request.getHealthWorkerId())
                .orElseThrow(() -> new ResourceNotFoundException("Health Worker not found with id: " + request.getHealthWorkerId()));

        VaccineScheduleItem scheduleItem = null;
        if (request.getScheduleCode() != null && !request.getScheduleCode().isBlank()) {
            scheduleItem = immunisationService.findItem(request.getScheduleCode())
                    .orElseThrow(() -> new BadRequestException("Unknown schedule code: " + request.getScheduleCode()));
        }
        boolean hasName = request.getName() != null && !request.getName().isBlank();
        if (!hasName && scheduleItem == null) {
            throw new BadRequestException("name or scheduleCode is required");
        }

        vaccination.setInfant(infant);
        vaccination.setHealthWorker(healthWorker);
        vaccination.setScheduleCode(scheduleItem != null ? scheduleItem.getCode() : null);
        vaccination.setName(hasName ? request.getName()
                : scheduleItem.getVaccineName() + " (dose " + scheduleItem.getDoseNumber() + ")");
        vaccination.setDescription(request.getDescription() != null ? request.getDescription() : "");
        vaccination.setAdministeredDate(request.getAdministeredDate());
        vaccination.setNextDueDate(request.getNextDueDate());
        if (request.getNextDueDate() == null && scheduleItem != null) {
            // Default to when the next outstanding scheduled dose falls due
            immunisationService.nextDueDateAfter(infant, scheduleItem.getCode())
                    .ifPresent(next -> vaccination.setNextDueDate(Dates.toDate(next)));
        }
        vaccination.setNotes(request.getNotes());
    }

    private List<Vaccination> inCaseload(List<Vaccination> vaccinations) {
        return caseloadGuard.filter(vaccinations, vaccination -> vaccination.getInfant().getMother());
    }

    private static String infantName(Infant infant) {
        return ((infant.getFirstName() != null ? infant.getFirstName() : "") + " "
                + (infant.getLastName() != null ? infant.getLastName() : "")).trim();
    }

    /**
     * Reminds the mother by SMS, and by email when she has one, that the next dose is due.
     *
     * @return whether at least one of the two went out; only then is the reminder marked as sent
     */
    @Override
    @Transactional
    public boolean remind(Vaccination vaccination) {
        Parent parent = vaccination.getInfant().getMother();
        String infantName = infantName(vaccination.getInfant());
        String dueDate = notificationService.formatDate(vaccination.getNextDueDate());

        boolean sent = notificationService.notifyParent(parent, SmsPurpose.VACCINATION_DUE, "vaccination.due",
                vaccination.getName(), infantName, dueDate).getStatus().isSent();

        if (parent.getEmail() != null && !parent.getEmail().isBlank()) {
            try {
                mailService.sendEmail(parent.getEmail(), "Vaccination Due Reminder", String.format(
                        "Dear Parent,\n\n"
                        + "This is a reminder that %s is due for %s vaccination on %s.\n\n"
                        + "Please contact your healthcare provider to schedule an appointment.\n\n"
                        + "Best regards,\n"
                        + "KuzaHealth Team",
                        infantName, vaccination.getName(), dueDate));
                sent = true;
            } catch (MailException e) {
                logger.error("Could not email vaccination reminder for vaccination {}", vaccination.getId(), e);
            }
        }

        if (sent) {
            vaccination.setNotificationSent(true);
            vaccinationRepository.save(vaccination);
        }
        return sent;
    }
}
