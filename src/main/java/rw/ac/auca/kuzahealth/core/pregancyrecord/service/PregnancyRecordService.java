package rw.ac.auca.kuzahealth.core.pregancyrecord.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.ac.auca.kuzahealth.core.pregancyrecord.entity.PregnancyRecord;
import rw.ac.auca.kuzahealth.core.pregancyrecord.exception.DuplicateResourceException;
import rw.ac.auca.kuzahealth.core.pregancyrecord.exception.ResourceNotFoundException;
import rw.ac.auca.kuzahealth.core.pregancyrecord.repository.PregnancyRecordRepository;
import rw.ac.auca.kuzahealth.core.parent.entity.Parent;


import java.util.List;
import java.util.UUID;
import java.util.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class PregnancyRecordService {
    
    private static final int GESTATION_WEEKS = 40;

    private final PregnancyRecordRepository pregnancyRecordRepository;
    
    public PregnancyRecord createPregnancyRecord(PregnancyRecord pregnancyRecord) {
        // Check if a record already exists for this parent and LMP
        if (pregnancyRecordRepository.existsByParentAndLastMenstrualPeriod(
                pregnancyRecord.getParent(), 
                pregnancyRecord.getLast_menstrual_period())) {
            throw new DuplicateResourceException(
                "Pregnancy record already exists for this parent and last menstrual period date");
        }
        
        return pregnancyRecordRepository.save(pregnancyRecord);
    }
    
    public PregnancyRecord updatePregnancyRecord(UUID id, PregnancyRecord updatedRecord) {
        PregnancyRecord existingRecord = pregnancyRecordRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pregnancy record not found with id: " + id));
            
        // Update fields
        existingRecord.setGravity(updatedRecord.getGravity());
        existingRecord.setParity(updatedRecord.getParity());
        existingRecord.setLast_menstrual_period(updatedRecord.getLast_menstrual_period());
        existingRecord.setMedical_history(updatedRecord.getMedical_history());
        existingRecord.setPregnancy_complications(updatedRecord.getPregnancy_complications());
        
        return pregnancyRecordRepository.save(existingRecord);
    }
    
    public PregnancyRecord getPregnancyRecord(UUID id) {
        return pregnancyRecordRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pregnancy record not found with id: " + id));
    }
    
    public List<PregnancyRecord> getAllPregnancyRecordsByParent(Parent parent) {
        return pregnancyRecordRepository.findByParentOrderByCreatedAtDesc(parent)
            .orElseThrow(() -> new ResourceNotFoundException("No pregnancy records found for parent"));
    }
    
    public void deletePregnancyRecord(UUID id) {
        if (!pregnancyRecordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pregnancy record not found with id: " + id);
        }
        pregnancyRecordRepository.deleteById(id);
    }
    
    public List<PregnancyRecord> getAllPregnancyRecords() {
        return pregnancyRecordRepository.findAll();
    }
    
    // Additional business logic methods
    
    public boolean hasActivePregnancy(Parent parent) {
        List<PregnancyRecord> records = pregnancyRecordRepository.findByParentOrderByCreatedAtDesc(parent)
            .orElse(List.of());
            
        if (records.isEmpty()) {
            return false;
        }
        
        PregnancyRecord latestRecord = records.get(0);
        Date lmp = latestRecord.getLast_menstrual_period();
        if (lmp == null) {
            return false;
        }
        
        LocalDate lmpDate = Instant.ofEpochMilli(lmp.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate today = LocalDate.now();

        // If LMP is in the future, treat as not active
        if (lmpDate.isAfter(today)) {
            return false;
        }

        long weeks = ChronoUnit.WEEKS.between(lmpDate, today);
        return weeks <= GESTATION_WEEKS;
    }
    
    public int calculateWeeksOfPregnancy(PregnancyRecord record) {
        if (record == null || record.getLast_menstrual_period() == null) {
            return 0;
        }
        Date lmp = record.getLast_menstrual_period();
        LocalDate lmpDate = Instant.ofEpochMilli(lmp.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate today = LocalDate.now();

        if (lmpDate.isAfter(today)) {
            return 0;
        }

        long weeks = ChronoUnit.WEEKS.between(lmpDate, today);
        return (int) weeks;
    }
}