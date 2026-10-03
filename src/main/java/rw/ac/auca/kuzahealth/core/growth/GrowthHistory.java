package rw.ac.auca.kuzahealth.core.growth;

import java.util.List;
import java.util.UUID;

/**
 * @param lowBirthWeight whether the recorded birth weight is under 2500 g; null when not recorded
 * @param zScoresAvailable whether WHO reference tables are installed on this server
 */
public record GrowthHistory(UUID infantId, Boolean lowBirthWeight, boolean zScoresAvailable,
        List<GrowthAssessment> measurements) {
}
