package rw.ac.auca.kuzahealth.controller.pregancyrecord;

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
import rw.ac.auca.kuzahealth.core.parent.entity.Parent;
import rw.ac.auca.kuzahealth.core.pregancyrecord.entity.PregnancyRecord;
import rw.ac.auca.kuzahealth.core.pregancyrecord.service.PregnancyRecordService;

@RestController
@RequestMapping("/api/pregnancy-records")
@RequiredArgsConstructor
public class PregnancyRecordController {

    private final PregnancyRecordService pregnancyRecordService;

    @PostMapping
    public ResponseEntity<PregnancyRecord> createPregnancyRecord(@RequestBody PregnancyRecord pregnancyRecord) {
        PregnancyRecord created = pregnancyRecordService.createPregnancyRecord(pregnancyRecord);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PregnancyRecord> updatePregnancyRecord(
            @PathVariable UUID id,
            @RequestBody PregnancyRecord pregnancyRecord) {
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
        Parent parent = new Parent(); // You'll need to get the parent properly
        parent.setId(parentId);
        List<PregnancyRecord> records = pregnancyRecordService.getAllPregnancyRecordsByParent(parent);
        return ResponseEntity.ok(records);
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
        Parent parent = new Parent(); // You'll need to get the parent properly
        parent.setId(parentId);
        boolean hasActive = pregnancyRecordService.hasActivePregnancy(parent);
        return ResponseEntity.ok(hasActive);
    }

    @GetMapping("/{id}/weeks")
    public ResponseEntity<Integer> getWeeksOfPregnancy(@PathVariable UUID id) {
        PregnancyRecord record = pregnancyRecordService.getPregnancyRecord(id);
        int weeks = pregnancyRecordService.calculateWeeksOfPregnancy(record);
        return ResponseEntity.ok(weeks);
    }
}
