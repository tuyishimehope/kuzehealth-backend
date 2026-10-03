package rw.ac.auca.kuzahealth.core.visit.repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import rw.ac.auca.kuzahealth.core.visit.entity.Visit;
import rw.ac.auca.kuzahealth.core.visit.enums.VisitStatus;

public interface VisitRepository extends JpaRepository<Visit, UUID>, JpaSpecificationExecutor<Visit> {
    List<Visit> findByParent_Id(UUID id);

    List<Visit> findByStatusAndReminderSentFalseAndScheduledTimeBetween(VisitStatus status, Date from, Date to);

    @Query("SELECT COUNT(v) FROM Visit v WHERE v.parent.assignedHealthWorker.id = :workerId "
            + "AND v.status = :scheduled AND v.scheduledTime BETWEEN :from AND :to")
    long countUpcomingForCaseload(@Param("workerId") UUID workerId, @Param("scheduled") VisitStatus scheduled,
            @Param("from") Date from, @Param("to") Date to);

    @Query("SELECT COUNT(v) FROM Visit v WHERE v.parent.assignedHealthWorker.id = :workerId AND (v.status = :missed "
            + "OR (v.status = :scheduled AND v.actualStartTime IS NULL AND v.scheduledTime < :cutoff))")
    long countMissedForCaseload(@Param("workerId") UUID workerId, @Param("missed") VisitStatus missed,
            @Param("scheduled") VisitStatus scheduled, @Param("cutoff") Date cutoff);

    /** Visits that were never started and whose time passed before the cutoff. */
    List<Visit> findByStatusAndActualStartTimeIsNullAndScheduledTimeBefore(VisitStatus status, Date cutoff);
}
