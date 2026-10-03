package rw.ac.auca.kuzahealth.core.growth;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A measurement together with what it means for the child's age.
 *
 * @param zScores WHO z-scores per indicator; empty when the reference tables are not
 *                installed or the child's sex or date of birth is unknown
 */
public record GrowthAssessment(UUID id, UUID infantId, LocalDate measuredAt, Long ageDays, Double weightKg,
        Double heightCm, Double headCircumferenceCm, Double muacCm, String notes, String recordedBy,
        Map<GrowthIndicator, Double> zScores, List<GrowthFlag> flags) {
}
