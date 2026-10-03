package rw.ac.auca.kuzahealth.core.immunisation;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.core.caseload.CaseloadGuard;
import rw.ac.auca.kuzahealth.core.exception.BadRequestException;
import rw.ac.auca.kuzahealth.core.exception.DuplicateResourceException;
import rw.ac.auca.kuzahealth.core.exception.ResourceNotFoundException;
import rw.ac.auca.kuzahealth.core.infant.entity.Infant;
import rw.ac.auca.kuzahealth.core.infant.repository.InfantRepository;
import rw.ac.auca.kuzahealth.core.parent.entity.Parent;
import rw.ac.auca.kuzahealth.core.vaccination.entity.Vaccination;
import rw.ac.auca.kuzahealth.core.vaccination.repository.VaccinationRepository;
import rw.ac.auca.kuzahealth.utils.Dates;

/**
 * Works out which routine doses an infant should have had by now, from the date of
 * birth and the configured schedule, and compares that with the vaccinations recorded.
 */
@Service
@RequiredArgsConstructor
public class ImmunisationService {

    /** Infants older than this are no longer checked for overdue routine doses. */
    private static final int FOLLOW_UP_AGE_DAYS = 730;

    private final VaccineScheduleItemRepository scheduleRepository;
    private final InfantRepository infantRepository;
    private final VaccinationRepository vaccinationRepository;
    private final CaseloadGuard caseloadGuard;

    @Value("${app.immunisation.overdue-after-days:28}")
    private int overdueAfterDays;

    @Value("${app.timezone:Africa/Kigali}")
    private String timezone;

    @Transactional(readOnly = true)
    public List<VaccineScheduleItem> getSchedule() {
        return scheduleRepository.findByActiveTrueOrderByDueAgeDaysAscCodeAsc();
    }

    @Transactional(readOnly = true)
    public Optional<VaccineScheduleItem> findItem(String code) {
        return code == null || code.isBlank() ? Optional.empty()
                : scheduleRepository.findByCode(code.trim().toUpperCase());
    }

    @Transactional
    public VaccineScheduleItem createItem(VaccineScheduleItemRequest request) {
        String code = request.getCode().toUpperCase();
        if (scheduleRepository.existsByCode(code)) {
            throw new DuplicateResourceException("A schedule item with code " + code + " already exists");
        }
        VaccineScheduleItem item = new VaccineScheduleItem();
        item.setCode(code);
        apply(request, item);
        return scheduleRepository.save(item);
    }

    @Transactional
    public VaccineScheduleItem updateItem(UUID id, VaccineScheduleItemRequest request) {
        VaccineScheduleItem item = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule item not found"));
        String code = request.getCode().toUpperCase();
        if (!code.equals(item.getCode()) && scheduleRepository.existsByCode(code)) {
            throw new DuplicateResourceException("A schedule item with code " + code + " already exists");
        }
        item.setCode(code);
        apply(request, item);
        return scheduleRepository.save(item);
    }

    /** Takes a dose out of the schedule. Recorded vaccinations that refer to it are kept. */
    @Transactional
    public void deactivateItem(UUID id) {
        VaccineScheduleItem item = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule item not found"));
        item.setActive(false);
        scheduleRepository.save(item);
    }

    /** Every scheduled dose for the infant with its status today. */
    @Transactional(readOnly = true)
    public List<ScheduledDose> scheduleFor(UUID infantId) {
        Infant infant = infantRepository.findById(infantId)
                .orElseThrow(() -> new ResourceNotFoundException("Infant not found with id: " + infantId));
        caseloadGuard.check(infant.getMother());
        if (infant.getDateOfBirth() == null) {
            throw new BadRequestException("The infant has no date of birth, so a schedule cannot be worked out");
        }
        return evaluate(infant, getSchedule(), vaccinationRepository.findByInfant_Id(infantId), today());
    }

    /**
     * The date the next outstanding dose after {@code givenCode} falls due for this infant,
     * used as the default "next due date" when a scheduled dose is recorded.
     */
    @Transactional(readOnly = true)
    public Optional<LocalDate> nextDueDateAfter(Infant infant, String givenCode) {
        LocalDate dateOfBirth = Dates.toLocalDate(infant.getDateOfBirth());
        if (dateOfBirth == null) {
            return Optional.empty();
        }
        List<VaccineScheduleItem> schedule = getSchedule();
        Optional<VaccineScheduleItem> given = schedule.stream()
                .filter(item -> item.getCode().equals(givenCode)).findFirst();
        if (given.isEmpty()) {
            return Optional.empty();
        }
        Map<String, Vaccination> recorded = match(schedule, vaccinationRepository.findByInfant_Id(infant.getId()));
        return schedule.stream()
                .filter(item -> item.getDueAgeDays() > given.get().getDueAgeDays())
                .filter(item -> !recorded.containsKey(item.getCode()))
                .map(item -> dateOfBirth.plusDays(item.getDueAgeDays()))
                .findFirst();
    }

    /** Infants up to two years old with overdue doses, optionally limited to one district. */
    @Transactional(readOnly = true)
    public List<OverdueInfant> overdue(String district) {
        LocalDate today = today();
        List<VaccineScheduleItem> schedule = getSchedule();
        List<Infant> infants = caseloadGuard.filter(infantRepository.findByDateOfBirthGreaterThanEqual(
                Dates.toDate(today.minusDays(FOLLOW_UP_AGE_DAYS))), Infant::getMother);
        if (infants.isEmpty()) {
            return List.of();
        }
        Map<UUID, List<Vaccination>> vaccinations = vaccinationRepository
                .findByInfant_IdIn(infants.stream().map(Infant::getId).toList()).stream()
                .collect(Collectors.groupingBy(v -> v.getInfant().getId()));

        List<OverdueInfant> result = new ArrayList<>();
        for (Infant infant : infants) {
            Parent mother = infant.getMother();
            if (district != null && !district.isBlank()
                    && (mother.getDistrict() == null || !mother.getDistrict().equalsIgnoreCase(district.trim()))) {
                continue;
            }
            List<ScheduledDose> overdueDoses = evaluate(infant, schedule,
                    vaccinations.getOrDefault(infant.getId(), List.of()), today).stream()
                    .filter(dose -> dose.status() == DoseStatus.OVERDUE)
                    .toList();
            if (!overdueDoses.isEmpty()) {
                result.add(new OverdueInfant(infant.getId(), name(infant.getFirstName(), infant.getLastName()),
                        Dates.toLocalDate(infant.getDateOfBirth()), mother.getId(), mother.getFullName(),
                        mother.getPhone(), mother.getDistrict(), overdueDoses));
            }
        }
        return result;
    }

    /** Share of eligible infants who received each scheduled dose, optionally for one district. */
    @Transactional(readOnly = true)
    public List<CoverageRow> coverage(String district) {
        LocalDate today = today();
        List<VaccineScheduleItem> schedule = getSchedule();
        List<Infant> infants = infantRepository.findAll().stream()
                .filter(infant -> infant.getDateOfBirth() != null)
                .filter(infant -> district == null || district.isBlank()
                        || district.trim().equalsIgnoreCase(infant.getMother().getDistrict()))
                .toList();
        Map<UUID, List<Vaccination>> vaccinations = infants.isEmpty() ? Map.of()
                : vaccinationRepository.findByInfant_IdIn(infants.stream().map(Infant::getId).toList()).stream()
                        .collect(Collectors.groupingBy(v -> v.getInfant().getId()));

        Map<String, long[]> counts = new HashMap<>(); // code -> {eligible, vaccinated}
        for (Infant infant : infants) {
            for (ScheduledDose dose : evaluate(infant, schedule,
                    vaccinations.getOrDefault(infant.getId(), List.of()), today)) {
                if (dose.status() == DoseStatus.UPCOMING) {
                    continue;
                }
                long[] count = counts.computeIfAbsent(dose.code(), key -> new long[2]);
                count[0]++;
                if (dose.status() == DoseStatus.GIVEN) {
                    count[1]++;
                }
            }
        }
        return schedule.stream().map(item -> {
            long[] count = counts.getOrDefault(item.getCode(), new long[2]);
            Double percent = count[0] == 0 ? null : Math.round(count[1] * 1000.0 / count[0]) / 10.0;
            return new CoverageRow(item.getCode(), item.getVaccineName(), item.getDoseNumber(), count[0], count[1],
                    percent);
        }).toList();
    }

    List<ScheduledDose> evaluate(Infant infant, List<VaccineScheduleItem> schedule, List<Vaccination> vaccinations,
            LocalDate today) {
        LocalDate dateOfBirth = Dates.toLocalDate(infant.getDateOfBirth());
        Map<String, Vaccination> recorded = match(schedule, vaccinations);

        List<ScheduledDose> doses = new ArrayList<>();
        for (VaccineScheduleItem item : schedule) {
            LocalDate dueDate = dateOfBirth.plusDays(item.getDueAgeDays());
            Vaccination given = recorded.get(item.getCode());
            if (given != null) {
                doses.add(new ScheduledDose(item.getCode(), item.getVaccineName(), item.getDoseNumber(), dueDate,
                        DoseStatus.GIVEN, null, Dates.toLocalDate(given.getAdministeredDate()), given.getId()));
            } else if (today.isBefore(dueDate)) {
                doses.add(new ScheduledDose(item.getCode(), item.getVaccineName(), item.getDoseNumber(), dueDate,
                        DoseStatus.UPCOMING, null, null, null));
            } else {
                long daysOverdue = ChronoUnit.DAYS.between(dueDate, today);
                DoseStatus status = daysOverdue > overdueAfterDays ? DoseStatus.OVERDUE : DoseStatus.DUE;
                doses.add(new ScheduledDose(item.getCode(), item.getVaccineName(), item.getDoseNumber(), dueDate,
                        status, daysOverdue, null, null));
            }
        }
        return doses;
    }

    /**
     * Pairs recorded vaccinations with schedule items: by schedule code when the record has
     * one, otherwise by a name that reads the same as the code ("Penta 1" matches PENTA1).
     */
    private static Map<String, Vaccination> match(List<VaccineScheduleItem> schedule, List<Vaccination> vaccinations) {
        Map<String, String> codeByAlias = new HashMap<>();
        for (VaccineScheduleItem item : schedule) {
            codeByAlias.put(normalise(item.getCode()), item.getCode());
        }
        Map<String, Vaccination> recorded = new HashMap<>();
        for (Vaccination vaccination : vaccinations) {
            String code = vaccination.getScheduleCode() != null
                    ? codeByAlias.get(normalise(vaccination.getScheduleCode()))
                    : codeByAlias.get(normalise(vaccination.getName()));
            if (code != null) {
                recorded.putIfAbsent(code, vaccination);
            }
        }
        return recorded;
    }

    private static String normalise(String value) {
        return value == null ? "" : value.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
    }

    private static String name(String first, String last) {
        return ((first != null ? first : "") + " " + (last != null ? last : "")).trim();
    }

    private LocalDate today() {
        return LocalDate.now(ZoneId.of(timezone));
    }

    private static void apply(VaccineScheduleItemRequest request, VaccineScheduleItem item) {
        item.setVaccineName(request.getVaccineName());
        item.setDoseNumber(request.getDoseNumber());
        item.setDueAgeDays(request.getDueAgeDays());
        item.setDescription(request.getDescription());
        if (request.getActive() != null) {
            item.setActive(request.getActive());
        }
    }
}
