package rw.ac.auca.kuzahealth.core.growth;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.core.caseload.CaseloadGuard;
import rw.ac.auca.kuzahealth.core.exception.BadRequestException;
import rw.ac.auca.kuzahealth.core.exception.ResourceNotFoundException;
import rw.ac.auca.kuzahealth.core.growth.GrowthReference.Sex;
import rw.ac.auca.kuzahealth.core.infant.entity.Infant;
import rw.ac.auca.kuzahealth.core.infant.repository.InfantRepository;
import rw.ac.auca.kuzahealth.utils.Dates;
import rw.ac.auca.kuzahealth.utils.SoftDeleter;

@Service
@RequiredArgsConstructor
public class GrowthService {

    private static final double LOW_BIRTH_WEIGHT_GRAMS = 2500;
    private static final double MUAC_SEVERE_CM = 11.5;
    private static final double MUAC_MODERATE_CM = 12.5;
    private static final long MUAC_MIN_AGE_DAYS = 183; // MUAC cut-offs apply from 6 months
    private static final long MUAC_MAX_AGE_DAYS = 1826; // to 59 months

    private final GrowthMeasurementRepository measurementRepository;
    private final InfantRepository infantRepository;
    private final GrowthReference reference;
    private final SoftDeleter softDeleter;
    private final CaseloadGuard caseloadGuard;

    @Transactional
    public GrowthAssessment record(UUID infantId, GrowthMeasurementRequest request, String recordedBy) {
        Infant infant = findInfant(infantId);
        GrowthMeasurement measurement = new GrowthMeasurement();
        measurement.setInfant(infant);
        measurement.setRecordedBy(recordedBy);
        apply(infant, request, measurement);
        measurementRepository.save(measurement);
        return assessOne(infant, measurement.getId());
    }

    @Transactional
    public GrowthAssessment update(UUID measurementId, GrowthMeasurementRequest request) {
        GrowthMeasurement measurement = findMeasurement(measurementId);
        apply(measurement.getInfant(), request, measurement);
        measurementRepository.save(measurement);
        return assessOne(measurement.getInfant(), measurementId);
    }

    @Transactional
    public void delete(UUID measurementId) {
        softDeleter.delete(GrowthMeasurement.class, findMeasurement(measurementId).getId());
    }

    /** All measurements of an infant, oldest first, each with its z-scores and flags. */
    @Transactional(readOnly = true)
    public GrowthHistory history(UUID infantId) {
        Infant infant = findInfant(infantId);
        Boolean lowBirthWeight = infant.getBirthWeight() == null ? null
                : infant.getBirthWeight() < LOW_BIRTH_WEIGHT_GRAMS;
        return new GrowthHistory(infantId, lowBirthWeight, reference.isAvailable(), assessAll(infant));
    }

    private GrowthAssessment assessOne(Infant infant, UUID measurementId) {
        return assessAll(infant).stream()
                .filter(assessment -> assessment.id().equals(measurementId))
                .findFirst()
                .orElseThrow();
    }

    private List<GrowthAssessment> assessAll(Infant infant) {
        LocalDate dateOfBirth = Dates.toLocalDate(infant.getDateOfBirth());
        Optional<Sex> sex = Sex.fromGender(infant.getGender());

        List<GrowthAssessment> assessments = new ArrayList<>();
        Double previousWeight = null;
        for (GrowthMeasurement m : measurementRepository
                .findByInfant_IdOrderByMeasuredAtAscCreatedAtAsc(infant.getId())) {
            LocalDate measuredAt = Dates.toLocalDate(m.getMeasuredAt());
            Long ageDays = dateOfBirth == null ? null : ChronoUnit.DAYS.between(dateOfBirth, measuredAt);

            Map<GrowthIndicator, Double> zScores = new EnumMap<>(GrowthIndicator.class);
            if (sex.isPresent()) {
                if (ageDays != null) {
                    score(zScores, GrowthIndicator.WEIGHT_FOR_AGE, sex.get(), ageDays, m.getWeightKg());
                    score(zScores, GrowthIndicator.LENGTH_FOR_AGE, sex.get(), ageDays, m.getHeightCm());
                    score(zScores, GrowthIndicator.HEAD_CIRCUMFERENCE_FOR_AGE, sex.get(), ageDays,
                            m.getHeadCircumferenceCm());
                }
                if (m.getHeightCm() != null) {
                    score(zScores, GrowthIndicator.WEIGHT_FOR_LENGTH, sex.get(), m.getHeightCm(), m.getWeightKg());
                }
            }

            assessments.add(new GrowthAssessment(m.getId(), infant.getId(), measuredAt, ageDays, m.getWeightKg(),
                    m.getHeightCm(), m.getHeadCircumferenceCm(), m.getMuacCm(), m.getNotes(), m.getRecordedBy(),
                    zScores, flags(m, ageDays, zScores, previousWeight)));
            if (m.getWeightKg() != null) {
                previousWeight = m.getWeightKg();
            }
        }
        return assessments;
    }

    private void score(Map<GrowthIndicator, Double> zScores, GrowthIndicator indicator, Sex sex, double x,
            Double value) {
        if (value != null) {
            reference.zScore(indicator, sex, x, value).ifPresent(z -> zScores.put(indicator, z));
        }
    }

    private static List<GrowthFlag> flags(GrowthMeasurement m, Long ageDays, Map<GrowthIndicator, Double> zScores,
            Double previousWeight) {
        List<GrowthFlag> flags = new ArrayList<>();
        Double wasting = zScores.get(GrowthIndicator.WEIGHT_FOR_LENGTH);
        boolean muacApplies = m.getMuacCm() != null && ageDays != null
                && ageDays >= MUAC_MIN_AGE_DAYS && ageDays <= MUAC_MAX_AGE_DAYS;

        if ((muacApplies && m.getMuacCm() < MUAC_SEVERE_CM) || (wasting != null && wasting < -3)) {
            flags.add(GrowthFlag.SEVERE_ACUTE_MALNUTRITION);
        } else if ((muacApplies && m.getMuacCm() < MUAC_MODERATE_CM) || (wasting != null && wasting < -2)) {
            flags.add(GrowthFlag.MODERATE_ACUTE_MALNUTRITION);
        }
        addBySeverity(flags, zScores.get(GrowthIndicator.WEIGHT_FOR_AGE), GrowthFlag.SEVERELY_UNDERWEIGHT,
                GrowthFlag.UNDERWEIGHT);
        addBySeverity(flags, zScores.get(GrowthIndicator.LENGTH_FOR_AGE), GrowthFlag.SEVERELY_STUNTED,
                GrowthFlag.STUNTED);
        if (wasting != null && wasting > 2) {
            flags.add(GrowthFlag.POSSIBLE_OVERWEIGHT);
        }
        if (previousWeight != null && m.getWeightKg() != null && m.getWeightKg() < previousWeight) {
            flags.add(GrowthFlag.WEIGHT_LOSS);
        }
        return flags;
    }

    private static void addBySeverity(List<GrowthFlag> flags, Double z, GrowthFlag severe, GrowthFlag moderate) {
        if (z == null) {
            return;
        }
        if (z < -3) {
            flags.add(severe);
        } else if (z < -2) {
            flags.add(moderate);
        }
    }

    private static void apply(Infant infant, GrowthMeasurementRequest request, GrowthMeasurement measurement) {
        if (request.getWeightKg() == null && request.getHeightCm() == null
                && request.getHeadCircumferenceCm() == null && request.getMuacCm() == null) {
            throw new BadRequestException("At least one measurement is required");
        }
        LocalDate dateOfBirth = Dates.toLocalDate(infant.getDateOfBirth());
        if (dateOfBirth != null && Dates.toLocalDate(request.getMeasuredAt()).isBefore(dateOfBirth)) {
            throw new BadRequestException("measuredAt is before the infant's date of birth");
        }
        measurement.setMeasuredAt(request.getMeasuredAt());
        measurement.setWeightKg(request.getWeightKg());
        measurement.setHeightCm(request.getHeightCm());
        measurement.setHeadCircumferenceCm(request.getHeadCircumferenceCm());
        measurement.setMuacCm(request.getMuacCm());
        measurement.setNotes(request.getNotes());
    }

    private Infant findInfant(UUID infantId) {
        Infant infant = infantRepository.findById(infantId)
                .orElseThrow(() -> new ResourceNotFoundException("Infant not found with id: " + infantId));
        caseloadGuard.check(infant.getMother());
        return infant;
    }

    private GrowthMeasurement findMeasurement(UUID id) {
        GrowthMeasurement measurement = measurementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Growth measurement not found with id: " + id));
        caseloadGuard.check(measurement.getInfant().getMother());
        return measurement;
    }
}
