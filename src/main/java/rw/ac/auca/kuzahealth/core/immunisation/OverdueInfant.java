package rw.ac.auca.kuzahealth.core.immunisation;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * An infant with at least one overdue dose, with the contact details needed to follow up.
 */
public record OverdueInfant(UUID infantId, String infantName, LocalDate dateOfBirth, UUID motherId,
        String motherName, String motherPhone, String district, List<ScheduledDose> overdueDoses) {
}
