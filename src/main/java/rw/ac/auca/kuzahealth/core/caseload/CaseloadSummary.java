package rw.ac.auca.kuzahealth.core.caseload;

import java.util.UUID;

/**
 * Size of a health worker's caseload and what needs attention in it.
 */
public record CaseloadSummary(UUID healthWorkerId, String healthWorkerName, long parents, long highRiskParents,
        long upcomingVisitsNext7Days, long missedVisits) {
}
