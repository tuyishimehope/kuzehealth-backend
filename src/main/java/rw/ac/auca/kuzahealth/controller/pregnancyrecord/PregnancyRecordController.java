package rw.ac.auca.kuzahealth.controller.pregnancyrecord;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.core.pregnancyrecord.dto.PregnancyRecordDto;
import rw.ac.auca.kuzahealth.core.pregnancyrecord.entity.PregnancyRecord;
import rw.ac.auca.kuzahealth.core.pregnancyrecord.service.PregnancyRecordService;

@RestController
@RequestMapping({ "/api/pregnancy-records", "/api/v1/pregnancy-records" })
@RequiredArgsConstructor
public class PregnancyRecordController {

    private final PregnancyRecordService pregnancyRecordService;

    @PostMapping
    public ResponseEntity<PregnancyRecord> createPregnancyRecord(@RequestBody PregnancyRecordDto pregnancyRecord) {
        PregnancyRecord created = pregnancyRecordService.createPregnancyRecord(pregnancyRecord);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PregnancyRecord> updatePregnancyRecord(
            @PathVariable UUID id,
            @RequestBody PregnancyRecordDto pregnancyRecord) {
        PregnancyRecord updated = pregnancyRecordService.updatePregnancyRecord(id, pregnancyRecord);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PregnancyRecord> getPregnancyRecord(@PathVariable UUID id) {
        PregnancyRecord record = pregnancyRecordService.getPregnancyRecord(id);
        return ResponseEntity.ok(record);
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<PregnancyRecord>> getPregnancyRecordsByParent(@PathVariable UUID parentId) {
        return ResponseEntity.ok(pregnancyRecordService.getAllPregnancyRecordsByParent(parentId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePregnancyRecord(@PathVariable UUID id) {
        pregnancyRecordService.deletePregnancyRecord(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<PregnancyRecord>> getAllPregnancyRecords() {
        List<PregnancyRecord> records = pregnancyRecordService.getAllPregnancyRecords();
        return ResponseEntity.ok(records);
    }

    @GetMapping("/parent/{parentId}/active")
    public ResponseEntity<Boolean> hasActivePregnancy(@PathVariable UUID parentId) {
        return ResponseEntity.ok(pregnancyRecordService.hasActivePregnancy(parentId));
    }

    @GetMapping("/{id}/weeks")
    public ResponseEntity<Integer> getWeeksOfPregnancy(@PathVariable UUID id) {
        PregnancyRecord record = pregnancyRecordService.getPregnancyRecord(id);
        int weeks = pregnancyRecordService.calculateWeeksOfPregnancy(record);
        return ResponseEntity.ok(weeks);
    }
}
