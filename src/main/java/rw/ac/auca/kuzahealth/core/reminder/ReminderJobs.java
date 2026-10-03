package rw.ac.auca.kuzahealth.core.reminder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.core.user.token.RefreshTokenService;

/**
 * Scheduled jobs. Timers run in a single application instance; if the service is ever
 * scaled to several instances, add a distributed lock before enabling reminders.
 */
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ReminderJobs {

    private static final Logger logger = LoggerFactory.getLogger(ReminderJobs.class);

    private final ReminderService reminderService;
    private final RefreshTokenService refreshTokenService;

    @Value("${app.reminders.enabled:false}")
    private boolean enabled;

    @Scheduled(cron = "${app.reminders.vaccination-cron}", zone = "${app.timezone}")
    public void vaccinationReminders() {
        if (enabled) {
            run("vaccination reminders", reminderService::sendVaccinationReminders);
        }
    }

    @Scheduled(cron = "${app.reminders.visit-cron}", zone = "${app.timezone}")
    public void visitReminders() {
        if (enabled) {
            run("visit reminders", reminderService::sendVisitReminders);
            run("missed visits", reminderService::markMissedVisits);
        }
    }

    @Scheduled(cron = "0 30 3 * * *", zone = "${app.timezone}")
    public void purgeExpiredRefreshTokens() {
        run("refresh token cleanup", refreshTokenService::deleteExpired);
    }

    private static void run(String name, java.util.function.IntSupplier job) {
        try {
            job.getAsInt();
        } catch (RuntimeException e) {
            logger.error("Scheduled job '{}' failed", name, e);
        }
    }
}
