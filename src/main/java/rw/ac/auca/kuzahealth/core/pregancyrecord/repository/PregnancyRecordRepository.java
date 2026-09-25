package rw.ac.auca.kuzahealth.core.pregancyrecord.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import rw.ac.auca.kuzahealth.core.pregancyrecord.entity.PregnancyRecord;
import rw.ac.auca.kuzahealth.core.parent.entity.Parent;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PregnancyRecordRepository extends JpaRepository<PregnancyRecord, UUID> {
    List<PregnancyRecord> findByParent(Parent parent);
    
    Optional<List<PregnancyRecord>> findByParentOrderByCreatedAtDesc(Parent parent);
    
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM PregnancyRecord p " +
           "WHERE p.parent = :parent AND p.last_menstrual_period = :lmp")
    boolean existsByParentAndLastMenstrualPeriod(@Param("parent") Parent parent, 
                                                @Param("lmp") Date lmp);
}