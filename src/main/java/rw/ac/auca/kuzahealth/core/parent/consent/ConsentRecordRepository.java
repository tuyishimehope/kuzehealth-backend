package rw.ac.auca.kuzahealth.core.parent.consent;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsentRecordRepository extends JpaRepository<ConsentRecord, UUID> {
    List<ConsentRecord> findByParent_IdOrderByCreatedAtDesc(UUID parentId);
}
