package rw.ac.auca.kuzahealth.core.pregnancyrecord.repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rw.ac.auca.kuzahealth.core.pregnancyrecord.entity.PregnancyRecord;

@Repository
public interface PregnancyRecordRepository extends JpaRepository<PregnancyRecord, UUID> {

    List<PregnancyRecord> findByParent_IdOrderByCreatedAtDesc(UUID parentId);

    boolean existsByParent_IdAndLastMenstrualPeriod(UUID parentId, Date lastMenstrualPeriod);

    List<PregnancyRecord> findByLastMenstrualPeriodGreaterThanEqual(Date earliest);
}
