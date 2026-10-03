package rw.ac.auca.kuzahealth.core.vaccination.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import rw.ac.auca.kuzahealth.core.healthworker.entity.HealthWorker;
import rw.ac.auca.kuzahealth.core.infant.entity.Infant;
import rw.ac.auca.kuzahealth.core.vaccination.entity.Vaccination;

/**
 * Repository for managing Vaccination entities
 */
@Repository
public interface VaccinationRepository
        extends JpaRepository<Vaccination, UUID>, JpaSpecificationExecutor<Vaccination> {

    List<Vaccination> findByInfant(Infant infant);

    List<Vaccination> findByInfant_Id(UUID infantId);

    List<Vaccination> findByInfant_IdIn(Collection<UUID> infantIds);

    List<Vaccination> findByHealthWorker(HealthWorker healthWorker);

    List<Vaccination> findByHealthWorker_Id(UUID healthWorkerId);

    /** Vaccinations whose next dose is due on or before the given date and has not been announced yet. */
    List<Vaccination> findByNextDueDateLessThanEqualAndNotificationSentFalse(Date date);

    List<Vaccination> findByNotificationSentFalseAndNextDueDateBetween(Date from, Date to);

    @Query("SELECT v FROM Vaccination v WHERE v.infant.mother.id = :parentId")
    List<Vaccination> findByParentId(@Param("parentId") UUID parentId);
}
