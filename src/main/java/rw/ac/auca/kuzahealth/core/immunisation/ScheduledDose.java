package rw.ac.auca.kuzahealth.core.immunisation;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * One expected dose for a particular infant.
 *
 * @param daysOverdue days past the due date, for doses that are DUE or OVERDUE
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ScheduledDose(String code, String vaccineName, int doseNumber, LocalDate dueDate, DoseStatus status,
        Long daysOverdue, LocalDate administeredDate, UUID vaccinationId) {
}
