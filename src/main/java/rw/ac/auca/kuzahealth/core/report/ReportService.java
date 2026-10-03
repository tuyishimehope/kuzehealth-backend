package rw.ac.auca.kuzahealth.core.report;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.core.immunisation.ImmunisationService;
import rw.ac.auca.kuzahealth.core.infant.entity.Infant;
import rw.ac.auca.kuzahealth.core.infant.repository.InfantRepository;
import rw.ac.auca.kuzahealth.core.parent.entity.Parent;
import rw.ac.auca.kuzahealth.core.parent.repository.ParentRepository;
import rw.ac.auca.kuzahealth.core.vaccination.entity.Vaccination;
import rw.ac.auca.kuzahealth.core.vaccination.repository.VaccinationRepository;
import rw.ac.auca.kuzahealth.core.visit.entity.Visit;
import rw.ac.auca.kuzahealth.core.visit.enums.VisitStatus;
import rw.ac.auca.kuzahealth.core.visit.repository.VisitRepository;
import rw.ac.auca.kuzahealth.utils.Csv;
import rw.ac.auca.kuzahealth.utils.Dates;

/**
 * Aggregated figures for dashboards and CSV exports for analysis.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private static final int ACTIVE_PREGNANCY_DAYS = 42 * 7;

    @PersistenceContext
    private EntityManager entityManager;

    private final ParentRepository parentRepository;
    private final InfantRepository infantRepository;
    private final VisitRepository visitRepository;
    private final VaccinationRepository vaccinationRepository;
    private final ImmunisationService immunisationService;

    @Value("${app.timezone:Africa/Kigali}")
    private String timezone;

    public record DistrictCount(String district, long parents, long highRiskParents) {
    }

    public record HealthWorkerVisits(UUID healthWorkerId, String healthWorkerName, long total,
            Map<String, Long> byStatus) {
    }

    public Map<String, Object> summary() {
        LocalDate today = LocalDate.now(ZoneId.of(timezone));
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("parents", count("SELECT COUNT(p) FROM Parent p"));
        summary.put("highRiskParents", count("SELECT COUNT(p) FROM Parent p WHERE p.isHighRisk = true"));
        summary.put("infants", count("SELECT COUNT(i) FROM Infant i"));
        summary.put("healthWorkers", count("SELECT COUNT(h) FROM HealthWorker h"));
        summary.put("activePregnancies", entityManager
                .createQuery("SELECT COUNT(r) FROM PregnancyRecord r WHERE r.lastMenstrualPeriod >= :earliest",
                        Long.class)
                .setParameter("earliest", Dates.toDate(today.minusDays(ACTIVE_PREGNANCY_DAYS)))
                .getSingleResult());

        Map<String, Long> visitsByStatus = new LinkedHashMap<>();
        for (Object[] row : entityManager
                .createQuery("SELECT v.status, COUNT(v) FROM Visit v GROUP BY v.status", Object[].class)
                .getResultList()) {
            visitsByStatus.put(((VisitStatus) row[0]).getLabel(), (Long) row[1]);
        }
        summary.put("visitsByStatus", visitsByStatus);

        summary.put("vaccinationsThisMonth", entityManager
                .createQuery("SELECT COUNT(v) FROM Vaccination v WHERE v.administeredDate >= :start", Long.class)
                .setParameter("start", Dates.toDate(today.withDayOfMonth(1)))
                .getSingleResult());
        summary.put("infantsWithOverdueDoses", immunisationService.overdue(null).size());
        return summary;
    }

    public List<DistrictCount> parentsByDistrict() {
        List<DistrictCount> result = new ArrayList<>();
        for (Object[] row : entityManager.createQuery(
                "SELECT p.district, COUNT(p), SUM(CASE WHEN p.isHighRisk = true THEN 1 ELSE 0 END) "
                        + "FROM Parent p GROUP BY p.district ORDER BY COUNT(p) DESC", Object[].class)
                .getResultList()) {
            result.add(new DistrictCount(row[0] != null ? (String) row[0] : "Unknown", (Long) row[1],
                    ((Number) row[2]).longValue()));
        }
        return result;
    }

    public List<HealthWorkerVisits> visitsByHealthWorker(Date from, Date to) {
        Map<UUID, HealthWorkerVisits> byWorker = new LinkedHashMap<>();
        for (Object[] row : entityManager.createQuery(
                "SELECT h.id, h.firstName, h.lastName, v.status, COUNT(v) FROM Visit v JOIN v.healthWorker h "
                        + "WHERE v.scheduledTime BETWEEN :from AND :to "
                        + "GROUP BY h.id, h.firstName, h.lastName, v.status ORDER BY h.lastName, h.firstName",
                Object[].class)
                .setParameter("from", from).setParameter("to", to).getResultList()) {
            UUID id = (UUID) row[0];
            String name = ((row[1] != null ? row[1] : "") + " " + (row[2] != null ? row[2] : "")).trim();
            long count = (Long) row[4];
            HealthWorkerVisits current = byWorker.computeIfAbsent(id,
                    key -> new HealthWorkerVisits(id, name, 0, new LinkedHashMap<>()));
            current.byStatus().put(((VisitStatus) row[3]).getLabel(), count);
            byWorker.put(id, new HealthWorkerVisits(id, name, current.total() + count, current.byStatus()));
        }
        return new ArrayList<>(byWorker.values());
    }

    public String parentsCsv() {
        Csv csv = new Csv().header("id", "firstName", "lastName", "phone", "email", "district", "sector", "cell",
                "village", "highRisk", "expectedDeliveryDate", "bloodGroup", "smsConsent", "preferredLanguage",
                "assignedHealthWorkerId", "createdAt");
        for (Parent p : parentRepository.findAll()) {
            csv.row(p.getId(), p.getFirstName(), p.getLastName(), p.getPhone(), p.getEmail(), p.getDistrict(),
                    p.getSector(), p.getCell(), p.getVillage(), p.isHighRisk(), p.getExpectedDeliveryDate(),
                    p.getBloodGroup(), p.isSmsConsent(), p.getPreferredLanguage(), p.getAssignedHealthWorkerId(),
                    p.getCreatedAt());
        }
        return csv.toString();
    }

    public String infantsCsv() {
        Csv csv = new Csv().header("id", "firstName", "lastName", "dateOfBirth", "gender", "birthWeightGrams",
                "birthHeightCm", "bloodGroup", "motherId", "createdAt");
        for (Infant i : infantRepository.findAll()) {
            csv.row(i.getId(), i.getFirstName(), i.getLastName(), i.getDateOfBirth(), i.getGender(),
                    i.getBirthWeight(), i.getBirthHeight(), i.getBloodGroup(), i.getMotherId(), i.getCreatedAt());
        }
        return csv.toString();
    }

    public String visitsCsv() {
        Csv csv = new Csv().header("id", "parentId", "healthWorkerId", "visitType", "status", "scheduledTime",
                "actualStartTime", "actualEndTime", "location", "modeOfCommunication");
        for (Visit v : visitRepository.findAll()) {
            csv.row(v.getId(), v.getParentId(), v.getHealthWorkerId(), v.getVisitType(), v.getStatus().getLabel(),
                    v.getScheduledTime(), v.getActualStartTime(), v.getActualEndTime(), v.getLocation(),
                    v.getModeOfCommunication());
        }
        return csv.toString();
    }

    public String vaccinationsCsv() {
        Csv csv = new Csv().header("id", "infantId", "healthWorkerId", "name", "scheduleCode", "administeredDate",
                "nextDueDate", "notificationSent");
        for (Vaccination v : vaccinationRepository.findAll()) {
            csv.row(v.getId(), v.getInfantId(), v.getHealthWorkerId(), v.getName(), v.getScheduleCode(),
                    v.getAdministeredDate(), v.getNextDueDate(), v.isNotificationSent());
        }
        return csv.toString();
    }

    private long count(String jpql) {
        return entityManager.createQuery(jpql, Long.class).getSingleResult();
    }
}
