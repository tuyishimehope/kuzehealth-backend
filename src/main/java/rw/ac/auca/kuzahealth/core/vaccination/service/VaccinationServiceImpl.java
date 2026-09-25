package rw.ac.auca.kuzahealth.core.vaccination.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rw.ac.auca.kuzahealth.core.healthworker.entity.HealthWorker;
import rw.ac.auca.kuzahealth.core.healthworker.repository.HealthWorkerRepository;
import rw.ac.auca.kuzahealth.core.infant.entity.Infant;
import rw.ac.auca.kuzahealth.core.infant.repository.InfantRepository;
import rw.ac.auca.kuzahealth.core.pregancyrecord.exception.ResourceNotFoundException;
import rw.ac.auca.kuzahealth.core.vaccination.dto.VaccinationRequest;
import rw.ac.auca.kuzahealth.core.vaccination.entity.Vaccination;
import rw.ac.auca.kuzahealth.core.vaccination.repository.VaccinationRepository;
import rw.ac.auca.kuzahealth.utils.MailService;

/**
 * Implementation of the VaccinationService interface
 */
@Service
public class VaccinationServiceImpl implements VaccinationService {

    private final VaccinationRepository vaccinationRepository;
    private final InfantRepository infantRepository;
    private final HealthWorkerRepository healthWorkerRepository;
    private final MailService mailService;

    @Autowired
    public VaccinationServiceImpl(
            VaccinationRepository vaccinationRepository,
            InfantRepository infantRepository,
            HealthWorkerRepository healthWorkerRepository,
            MailService mailService) {
        this.vaccinationRepository = vaccinationRepository;
        this.infantRepository = infantRepository;
        this.healthWorkerRepository = healthWorkerRepository;
        this.mailService = mailService;
    }

    @Override
    public Vaccination createVaccination(VaccinationRequest request) {
        Infant infant = infantRepository.findById(request.getInfantId())
                .orElseThrow(() -> new ResourceNotFoundException("Infant not found with id: " + request.getInfantId()));

        HealthWorker healthWorker = healthWorkerRepository.findById(request.getHealthWorkerId())
                .orElseThrow(() -> new ResourceNotFoundException("Health Worker not found with id: " + request.getHealthWorkerId()));

        Vaccination vaccination = new Vaccination();
        vaccination.setInfant(infant);
        vaccination.setHealthWorker(healthWorker);
        vaccination.setName(request.getName());
        vaccination.setDescription(request.getDescription());
        vaccination.setAdministeredDate(request.getAdministeredDate());
        vaccination.setNextDueDate(request.getNextDueDate());
        vaccination.setNotes(request.getNotes());

        return vaccinationRepository.save(vaccination);
    }

    @Override
    public Vaccination updateVaccination(UUID id, VaccinationRequest request) {
        Vaccination vaccination = findById(id);

        Infant infant = infantRepository.findById(request.getInfantId())
                .orElseThrow(() -> new ResourceNotFoundException("Infant not found with id: " + request.getInfantId()));

        HealthWorker healthWorker = healthWorkerRepository.findById(request.getHealthWorkerId())
                .orElseThrow(() -> new ResourceNotFoundException("Health Worker not found with id: " + request.getHealthWorkerId()));

        vaccination.setInfant(infant);
        vaccination.setHealthWorker(healthWorker);
        vaccination.setName(request.getName());
        vaccination.setDescription(request.getDescription());
        vaccination.setAdministeredDate(request.getAdministeredDate());
        vaccination.setNextDueDate(request.getNextDueDate());
        vaccination.setNotes(request.getNotes());

        return vaccinationRepository.save(vaccination);
    }

    @Override
    public Vaccination findById(UUID id) {
        return vaccinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vaccination not found with id: " + id));
    }

    @Override
    public List<Vaccination> findAll() {
        return vaccinationRepository.findAll();
    }

    @Override
    public List<Vaccination> findByInfant(Infant infant) {
        return vaccinationRepository.findByInfant(infant);
    }

    @Override
    public List<Vaccination> findByInfantId(UUID infantId) {
        return vaccinationRepository.findByInfant_Id(infantId);
    }

    @Override
    public List<Vaccination> findByHealthWorker(HealthWorker healthWorker) {
        return vaccinationRepository.findByHealthWorker(healthWorker);
    }

    @Override
    public List<Vaccination> findByHealthWorkerId(UUID healthWorkerId) {
        return vaccinationRepository.findByHealthWorker_Id(healthWorkerId);
    }

    @Override
    public List<Vaccination> findByParentId(UUID parentId) {
        return vaccinationRepository.findByParentId(parentId);
    }

    @Override
    public List<Vaccination> findDueVaccinations(Date date) {
        return vaccinationRepository.findByNextDueDateLessThanEqualAndNotificationSentFalse(date);
    }

    @Override
    public int sendDueVaccinationNotifications(Date date) {
        List<Vaccination> dueVaccinations = findDueVaccinations(date);
        int notificationsSent = 0;

        for (Vaccination vaccination : dueVaccinations) {
            String parentEmail = vaccination.getInfant().getMother().getEmail();
            String infantName = vaccination.getInfant().getFirstName() + " " + vaccination.getInfant().getLastName();
            String vaccinationName = vaccination.getName();

            // Send email notification
            sendVaccinationDueNotification(parentEmail, infantName, vaccinationName, vaccination.getNextDueDate());

            // Update notification status
            vaccination.setNotificationSent(true);
            vaccinationRepository.save(vaccination);

            notificationsSent++;
        }

        return notificationsSent;
    }

    @Override
    public void deleteById(UUID id) {
        if (!vaccinationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vaccination not found with id: " + id);
        }
        vaccinationRepository.deleteById(id);
    }

    /**
     * Send a notification email to a parent about a due vaccination
     * 
     * @param email the parent's email
     * @param infantName the infant's name
     * @param vaccinationName the vaccination name
     * @param dueDate the due date
     */
    private void sendVaccinationDueNotification(String email, String infantName, String vaccinationName, Date dueDate) {
        // Create the email
        String subject = "Vaccination Due Reminder";
        String message = String.format(
                "Dear Parent,\n\n" +
                "This is a reminder that %s is due for %s vaccination on %s.\n\n" +
                "Please contact your healthcare provider to schedule an appointment.\n\n" +
                "Best regards,\n" +
                "KuzaHealth Team",
                infantName, vaccinationName, dueDate.toString());

        // Send the email using the mail service
        mailService.sendEmail(email, subject, message);
    }
}
