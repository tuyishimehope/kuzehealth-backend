package rw.ac.auca.kuzahealth.core.pregnancyrecord.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.core.caseload.CaseloadGuard;
import rw.ac.auca.kuzahealth.core.exception.BadRequestException;
import rw.ac.auca.kuzahealth.core.exception.DuplicateResourceException;
import rw.ac.auca.kuzahealth.core.exception.ResourceNotFoundException;
import rw.ac.auca.kuzahealth.core.parent.entity.Parent;
import rw.ac.auca.kuzahealth.core.parent.repository.ParentRepository;
import rw.ac.auca.kuzahealth.core.pregnancyrecord.dto.PregnancyRecordDto;
import rw.ac.auca.kuzahealth.core.pregnancyrecord.entity.PregnancyRecord;
import rw.ac.auca.kuzahealth.core.pregnancyrecord.repository.PregnancyRecordRepository;
import rw.ac.auca.kuzahealth.utils.SoftDeleter;
import rw.ac.auca.kuzahealth.utils.Dates;

@Service
@RequiredArgsConstructor
@Transactional
public class PregnancyRecordService {

    private static final int GESTATION_WEEKS = 40;
    private final PregnancyRecordRepository pregnancyRecordRepository;
    private final ParentRepository parentRepository;
    private final SoftDeleter softDeleter;
    private final CaseloadGuard caseloadGuard;

    public PregnancyRecord createPregnancyRecord(PregnancyRecordDto request) {
        if (request.getParentId() == null) {
            throw new BadRequestException("parentId is required");
        }
        Parent parent = parentRepository.findById(request.getParentId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found with id: " + request.getParentId()));
        caseloadGuard.check(parent);

        if (request.getLastMenstrualPeriod() != null && pregnancyRecordRepository
                .existsByParent_IdAndLastMenstrualPeriod(parent.getId(), request.getLastMenstrualPeriod())) {
            throw new DuplicateResourceException(
                "Pregnancy record already exists for this parent and last menstrual period date");
        }

        PregnancyRecord record = new PregnancyRecord();
        record.setParent(parent);
        apply(request, record);

        // Naegele's rule: the expected delivery date is 280 days after the LMP
        LocalDate lmp = toLocalDate(request.getLastMenstrualPeriod());
        if (lmp != null && parent.getExpectedDeliveryDate() == null) {
            parent.setExpectedDeliveryDate(Dates.toDate(lmp.plusDays(GESTATION_WEEKS * 7L)));
            parentRepository.save(parent);
        }
        return pregnancyRecordRepository.save(record);
    }

    public PregnancyRecord updatePregnancyRecord(UUID id, PregnancyRecordDto request) {
        PregnancyRecord existingRecord = getPregnancyRecord(id);
        apply(request, existingRecord);
        return pregnancyRecordRepository.save(existingRecord);
    }

    @Transactional(readOnly = true)
    public PregnancyRecord getPregnancyRecord(UUID id) {
        PregnancyRecord record = pregnancyRecordRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pregnancy record not found with id: " + id));
        caseloadGuard.check(record.getParent());
        return record;
    }

    @Transactional(readOnly = true)
    public List<PregnancyRecord> getAllPregnancyRecordsByParent(UUID parentId) {
        return caseloadGuard.filter(pregnancyRecordRepository.findByParent_IdOrderByCreatedAtDesc(parentId),
                PregnancyRecord::getParent);
    }

    public void deletePregnancyRecord(UUID id) {
        softDeleter.delete(PregnancyRecord.class, getPregnancyRecord(id).getId());
    }

    @Transactional(readOnly = true)
    public List<PregnancyRecord> getAllPregnancyRecords() {
        return caseloadGuard.filter(pregnancyRecordRepository.findAll(), PregnancyRecord::getParent);
    }

    @Transactional(readOnly = true)
    public boolean hasActivePregnancy(UUID parentId) {
        List<PregnancyRecord> records = pregnancyRecordRepository.findByParent_IdOrderByCreatedAtDesc(parentId);
        return !records.isEmpty() && isActive(records.get(0));
    }

    /** A pregnancy is treated as active from the LMP until 40 weeks later. */
    public boolean isActive(PregnancyRecord record) {
        LocalDate lmp = toLocalDate(record.getLastMenstrualPeriod());
        LocalDate today = LocalDate.now();
        return lmp != null && !lmp.isAfter(today) && ChronoUnit.WEEKS.between(lmp, today) <= GESTATION_WEEKS;
    }

    public int calculateWeeksOfPregnancy(PregnancyRecord record) {
        LocalDate lmp = record == null ? null : toLocalDate(record.getLastMenstrualPeriod());
        LocalDate today = LocalDate.now();
        if (lmp == null || lmp.isAfter(today)) {
            return 0;
        }
        return (int) ChronoUnit.WEEKS.between(lmp, today);
    }

    public static LocalDate toLocalDate(Date date) {
        return Dates.toLocalDate(date);
    }

    private static void apply(PregnancyRecordDto request, PregnancyRecord record) {
        record.setGravity(request.getGravity());
        record.setParity(request.getParity());
        record.setLastMenstrualPeriod(request.getLastMenstrualPeriod());
        record.setMedicalHistory(request.getMedicalHistory());
        record.setPregnancyComplications(request.getPregnancyComplications());
    }
}
