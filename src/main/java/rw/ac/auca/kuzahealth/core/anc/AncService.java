package rw.ac.auca.kuzahealth.core.anc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.core.caseload.CaseloadGuard;
import rw.ac.auca.kuzahealth.core.exception.BadRequestException;
import rw.ac.auca.kuzahealth.core.exception.ResourceNotFoundException;
import rw.ac.auca.kuzahealth.core.healthworker.entity.HealthWorker;
import rw.ac.auca.kuzahealth.core.healthworker.repository.HealthWorkerRepository;
import rw.ac.auca.kuzahealth.core.parent.entity.Parent;
import rw.ac.auca.kuzahealth.core.pregnancyrecord.entity.PregnancyRecord;
import rw.ac.auca.kuzahealth.core.pregnancyrecord.repository.PregnancyRecordRepository;
import rw.ac.auca.kuzahealth.core.visit.entity.Visit;
import rw.ac.auca.kuzahealth.core.visit.enums.VisitStatus;
import rw.ac.auca.kuzahealth.core.visit.repository.VisitRepository;
import rw.ac.auca.kuzahealth.utils.Dates;

/**
 * Antenatal care tracking: expected delivery date, gestational age, the recommended
 * contact schedule and how the recorded visits line up with it, and risk flags.
 */
@Service
@RequiredArgsConstructor
public class AncService {

    public static final String ANC_VISIT_TYPE = "ANC";

    private static final int PREGNANCY_DAYS = 280;
    private static final int ACTIVE_UNTIL_DAYS = 42 * 7;
    /** A visit counts towards a contact when it falls within this many days of it. */
    private static final int MATCH_WINDOW_DAYS = 14;
    private static final int GRAND_MULTIPARITY = 5;
    private static final Pattern ANC_TYPE = Pattern.compile("(?i).*\\b(anc|antenatal|prenatal)\\b.*");

    private final PregnancyRecordRepository pregnancyRecordRepository;
    private final VisitRepository visitRepository;
    private final HealthWorkerRepository healthWorkerRepository;
    private final CaseloadGuard caseloadGuard;

    /** Gestational weeks of the recommended contacts (WHO 2016 model: eight contacts). */
    @Value("${app.anc.contact-weeks:12,20,26,30,34,36,38,40}")
    private List<Integer> contactWeeks;

    @Value("${app.timezone:Africa/Kigali}")
    private String timezone;

    @Transactional(readOnly = true)
    public AncPlan plan(UUID pregnancyRecordId) {
        return plan(findRecord(pregnancyRecordId), today());
    }

    /** Active pregnancies that carry at least one risk flag, soonest delivery first. */
    @Transactional(readOnly = true)
    public List<AncPlan> highRisk() {
        LocalDate today = today();
        Date earliestLmp = Dates.toDate(today.minusDays(ACTIVE_UNTIL_DAYS));
        return caseloadGuard.filter(pregnancyRecordRepository.findByLastMenstrualPeriodGreaterThanEqual(earliestLmp),
                PregnancyRecord::getParent).stream()
                .map(record -> plan(record, today))
                .filter(plan -> plan.active() && !plan.riskFlags().isEmpty())
                .sorted((a, b) -> a.expectedDeliveryDate().compareTo(b.expectedDeliveryDate()))
                .toList();
    }

    /**
     * Books an antenatal visit for every remaining contact that has none yet.
     * Parents are not messaged for each booking; the visit reminders cover that.
     */
    @Transactional
    public List<Visit> scheduleRemaining(UUID pregnancyRecordId, AncScheduleRequest request) {
        PregnancyRecord record = findRecord(pregnancyRecordId);
        HealthWorker healthWorker = healthWorkerRepository.findById(request.getHealthWorkerId())
                .orElseThrow(() -> new ResourceNotFoundException("HealthWorker not found"));
        LocalDate today = today();
        LocalTime time = request.getTime() != null ? request.getTime() : LocalTime.of(9, 0);

        List<Visit> created = new ArrayList<>();
        for (AncContact contact : plan(record, today).contacts()) {
            if (contact.visitId() != null || !contact.dueDate().isAfter(today)) {
                continue;
            }
            Visit visit = new Visit();
            visit.setScheduledTime(Date.from(contact.dueDate().atTime(time).atZone(ZoneId.of(timezone)).toInstant()));
            visit.setVisitType(ANC_VISIT_TYPE);
            visit.setLocation(request.getLocation());
            visit.setModeOfCommunication(request.getModeOfCommunication());
            visit.setSummary("Antenatal contact " + contact.number() + " (week " + contact.gestationalWeek() + ")");
            visit.setHealthWorker(healthWorker);
            visit.setParent(record.getParent());
            created.add(visitRepository.save(visit));
        }
        return created;
    }

    private AncPlan plan(PregnancyRecord record, LocalDate today) {
        LocalDate lmp = Dates.toLocalDate(record.getLastMenstrualPeriod());
        if (lmp == null) {
            throw new BadRequestException("The pregnancy record has no last menstrual period date");
        }
        Parent parent = record.getParent();
        long days = Math.max(ChronoUnit.DAYS.between(lmp, today), 0);
        int weeks = (int) (days / 7);
        boolean active = !lmp.isAfter(today) && days <= ACTIVE_UNTIL_DAYS;

        return new AncPlan(record.getId(), parent.getId(), parent.getFullName(), lmp, lmp.plusDays(PREGNANCY_DAYS),
                weeks, (int) (days % 7), trimester(weeks), active, riskFlags(record, parent, weeks),
                contacts(lmp, today, visitRepository.findByParent_Id(parent.getId())));
    }

    private List<AncContact> contacts(LocalDate lmp, LocalDate today, List<Visit> visits) {
        List<Visit> ancVisits = visits.stream()
                .filter(visit -> visit.getVisitType() != null && ANC_TYPE.matcher(visit.getVisitType()).matches())
                .filter(visit -> visit.getStatus() != VisitStatus.CANCELLED)
                .toList();
        Set<UUID> used = new HashSet<>();

        List<AncContact> contacts = new ArrayList<>();
        int number = 1;
        for (int week : contactWeeks) {
            LocalDate dueDate = lmp.plusWeeks(week);
            Visit match = closest(ancVisits, dueDate, used);

            AncContactStatus status;
            if (match != null && match.getStatus() == VisitStatus.COMPLETED) {
                status = AncContactStatus.ATTENDED;
            } else if (match != null && match.getStatus() != VisitStatus.MISSED
                    && !visitDate(match).isBefore(today)) {
                status = AncContactStatus.SCHEDULED;
            } else if (today.isBefore(dueDate)) {
                status = AncContactStatus.UPCOMING;
            } else if (today.isAfter(dueDate.plusDays(MATCH_WINDOW_DAYS))) {
                status = AncContactStatus.MISSED;
            } else {
                status = AncContactStatus.DUE;
            }
            contacts.add(new AncContact(number++, week, dueDate, status, match != null ? match.getId() : null));
        }
        return contacts;
    }

    /** The unused antenatal visit nearest to the contact date, if it is within the matching window. */
    private Visit closest(List<Visit> visits, LocalDate dueDate, Set<UUID> used) {
        Visit best = null;
        long bestDistance = Long.MAX_VALUE;
        for (Visit visit : visits) {
            long distance = Math.abs(ChronoUnit.DAYS.between(dueDate, visitDate(visit)));
            if (distance <= MATCH_WINDOW_DAYS && distance < bestDistance && !used.contains(visit.getId())) {
                best = visit;
                bestDistance = distance;
            }
        }
        if (best != null) {
            used.add(best.getId());
        }
        return best;
    }

    private LocalDate visitDate(Visit visit) {
        return visit.getScheduledTime().toInstant().atZone(ZoneId.of(timezone)).toLocalDate();
    }

    private static List<RiskFlag> riskFlags(PregnancyRecord record, Parent parent, int weeks) {
        List<RiskFlag> flags = new ArrayList<>();
        if (parent.isHighRisk()) {
            flags.add(RiskFlag.MARKED_HIGH_RISK);
        }
        if (record.getParity() >= GRAND_MULTIPARITY) {
            flags.add(RiskFlag.GRAND_MULTIPARITY);
        }
        String complications = record.getPregnancyComplications();
        if (complications != null && !complications.isBlank()
                && !complications.trim().matches("(?i)(none|no|n/?a|nil|-)")) {
            flags.add(RiskFlag.PREVIOUS_COMPLICATIONS);
        }
        String notes = ((record.getMedicalHistory() != null ? record.getMedicalHistory() : "") + " "
                + (complications != null ? complications : "")).toLowerCase();
        addIfMentioned(flags, notes, RiskFlag.HYPERTENSIVE_DISORDER, "hypertens", "eclampsia", "high blood pressure");
        addIfMentioned(flags, notes, RiskFlag.DIABETES, "diabet");
        addIfMentioned(flags, notes, RiskFlag.HIV, "hiv");
        addIfMentioned(flags, notes, RiskFlag.ANAEMIA, "anaemi", "anemi");
        addIfMentioned(flags, notes, RiskFlag.PREVIOUS_CAESAREAN, "caesarean", "cesarean", "c-section", "c section");
        addIfMentioned(flags, notes, RiskFlag.MULTIPLE_PREGNANCY, "twin", "triplet", "multiple pregnan");
        addIfMentioned(flags, notes, RiskFlag.BLEEDING, "bleeding", "haemorrhage", "hemorrhage");
        if (weeks >= 42) {
            flags.add(RiskFlag.POST_TERM);
        }
        return flags;
    }

    private static void addIfMentioned(List<RiskFlag> flags, String notes, RiskFlag flag, String... keywords) {
        for (String keyword : keywords) {
            if (notes.contains(keyword)) {
                flags.add(flag);
                return;
            }
        }
    }

    private static int trimester(int weeks) {
        if (weeks < 13) {
            return 1;
        }
        return weeks < 27 ? 2 : 3;
    }

    private PregnancyRecord findRecord(UUID id) {
        PregnancyRecord record = pregnancyRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pregnancy record not found with id: " + id));
        caseloadGuard.check(record.getParent());
        return record;
    }

    private LocalDate today() {
        return LocalDate.now(ZoneId.of(timezone));
    }
}
