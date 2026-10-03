package rw.ac.auca.kuzahealth.core.anc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Where a pregnancy stands today.
 *
 * @param gestationalAgeWeeks completed weeks since the last menstrual period
 * @param gestationalAgeDays  days beyond the completed weeks
 * @param active              whether the pregnancy is still within 42 weeks of the LMP
 */
public record AncPlan(UUID pregnancyRecordId, UUID parentId, String parentName, LocalDate lastMenstrualPeriod,
        LocalDate expectedDeliveryDate, int gestationalAgeWeeks, int gestationalAgeDays, int trimester,
        boolean active, List<RiskFlag> riskFlags, List<AncContact> contacts) {
}
