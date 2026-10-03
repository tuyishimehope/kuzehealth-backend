package rw.ac.auca.kuzahealth.controller.reminder;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.core.reminder.ReminderService;

/**
 * Lets an administrator see whether automatic reminders are on and run them on demand.
 */
@RestController
@RequestMapping("/api/v1/reminders")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    @Value("${app.reminders.enabled:false}")
    private boolean enabled;

    @GetMapping("/status")
    public Map<String, Object> status() {
        return Map.of("automaticRemindersEnabled", enabled);
    }

    /** Runs all reminder jobs now, whether or not the schedule is enabled. Sends real SMS. */
    @PostMapping("/run")
    public Map<String, Integer> run() {
        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("vaccinationRemindersSent", reminderService.sendVaccinationReminders());
        result.put("visitRemindersSent", reminderService.sendVisitReminders());
        result.put("visitsMarkedMissed", reminderService.markMissedVisits());
        return result;
    }
}
