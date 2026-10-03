package rw.ac.auca.kuzahealth.core.reminder;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.core.notification.NotificationService;
import rw.ac.auca.kuzahealth.core.notification.SmsPurpose;
import rw.ac.auca.kuzahealth.core.vaccination.entity.Vaccination;
import rw.ac.auca.kuzahealth.core.vaccination.repository.VaccinationRepository;
import rw.ac.auca.kuzahealth.core.vaccination.service.VaccinationService;
import rw.ac.auca.kuzahealth.core.visit.entity.Visit;
import rw.ac.auca.kuzahealth.core.visit.enums.VisitStatus;
import rw.ac.auca.kuzahealth.core.visit.repository.VisitRepository;

/**
 * The work behind the scheduled reminder jobs. Each method is safe to run repeatedly:
 * a reminder is flagged once sent and is not sent again.
 */
@Service
@RequiredArgsConstructor
public class ReminderService {

    private static final Logger logger = LoggerFactory.getLogger(ReminderService.class);

    private final VaccinationRepository vaccinationRepository;
    private final VaccinationService vaccinationService;
    private final VisitRepository visitRepository;
    private final NotificationService notificationService;

    @Value("${app.timezone:Africa/Kigali}")
    private String timezone;

    @Value("${app.reminders.vaccination-lead-days:2}")
    private int vaccinationLeadDays;

    @Value("${app.reminders.vaccination-max-overdue-days:30}")
    private int vaccinationMaxOverdueDays;

    @Value("${app.reminders.visit-lead-hours:24}")
    private long visitLeadHours;

    @Value("${app.reminders.missed-after-hours:24}")
    private long missedAfterHours;

    /** Reminds mothers about doses that fall due soon or became due recently. */
    @Transactional
    public int sendVaccinationReminders() {
        LocalDate today = LocalDate.now(ZoneId.of(timezone));
        Date from = java.sql.Date.valueOf(today.minusDays(vaccinationMaxOverdueDays));
        Date to = java.sql.Date.valueOf(today.plusDays(vaccinationLeadDays));

        int sent = 0;
        for (Vaccination vaccination : vaccinationRepository.findByNotificationSentFalseAndNextDueDateBetween(from, to)) {
            if (vaccinationService.remind(vaccination)) {
                sent++;
            }
        }
        logger.info("Vaccination reminders sent: {}", sent);
        return sent;
    }

    /** Reminds parents about scheduled visits that start within the lead time. */
    @Transactional
    public int sendVisitReminders() {
        Date now = new Date();
        Date until = new Date(now.getTime() + TimeUnit.HOURS.toMillis(visitLeadHours));
        List<Visit> visits = visitRepository.findByStatusAndReminderSentFalseAndScheduledTimeBetween(
                VisitStatus.SCHEDULED, now, until);

        int sent = 0;
        for (Visit visit : visits) {
            boolean delivered = notificationService.notifyParent(visit.getParent(), SmsPurpose.VISIT_REMINDER,
                    "visit.reminder", visit.getVisitType(),
                    notificationService.formatDate(visit.getScheduledTime()),
                    notificationService.formatTime(visit.getScheduledTime()), visit.getLocation())
                    .getStatus().isSent();
            // Flag it either way so a parent without consent or phone is not retried every hour
            visit.setReminderSent(true);
            visitRepository.save(visit);
            if (delivered) {
                sent++;
            }
        }
        logger.info("Visit reminders sent: {}", sent);
        return sent;
    }

    /** Marks visits that were never started as missed and tells the parent how to reschedule. */
    @Transactional
    public int markMissedVisits() {
        Date cutoff = new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(missedAfterHours));
        List<Visit> visits = visitRepository.findByStatusAndActualStartTimeIsNullAndScheduledTimeBefore(
                VisitStatus.SCHEDULED, cutoff);

        for (Visit visit : visits) {
            visit.setStatus(VisitStatus.MISSED);
            visitRepository.save(visit);
            notificationService.notifyParent(visit.getParent(), SmsPurpose.VISIT_MISSED, "visit.missed",
                    notificationService.formatDate(visit.getScheduledTime()));
        }
        logger.info("Visits marked as missed: {}", visits.size());
        return visits.size();
    }
}
