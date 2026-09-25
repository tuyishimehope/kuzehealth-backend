package rw.ac.auca.kuzahealth.core.vaccination.repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import rw.ac.auca.kuzahealth.core.healthworker.entity.HealthWorker;
import rw.ac.auca.kuzahealth.core.infant.entity.Infant;
import rw.ac.auca.kuzahealth.core.vaccination.entity.Vaccination;

/**
 * Repository for Vaccination entity
 */
@Repository
public interface VaccinationRepository extends JpaRepository<Vaccination, UUID> {
    
    /**
     * Find all vaccinations for a specific infant
     * 
     * @param infant the infant
     * @return list of vaccinations for the infant
     */
    List<Vaccination> findByInfant(Infant infant);
    
    /**
     * Find all vaccinations for a specific infant by ID
     * 
     * @param infantId the ID of the infant
     * @return list of vaccinations for the infant
     */
    List<Vaccination> findByInfant_Id(UUID infantId);
    
    /**
     * Find all vaccinations administered by a specific health worker
     * 
     * @param healthWorker the health worker
     * @return list of vaccinations administered by the health worker
     */
    List<Vaccination> findByHealthWorker(HealthWorker healthWorker);
    
    /**
     * Find all vaccinations administered by a specific health worker by ID
     * 
     * @param healthWorkerId the ID of the health worker
     * @return list of vaccinations administered by the health worker
     */
    List<Vaccination> findByHealthWorker_Id(UUID healthWorkerId);
    
    /**
     * Find all vaccinations with a next due date before or on the specified date
     * and notification not yet sent
     * 
     * @param date the date to check against
     * @return list of due vaccinations
     */
    List<Vaccination> findByNextDueDateLessThanEqualAndNotificationSentFalse(Date date);
    
    /**
     * Find all vaccinations for infants of a specific parent
     * 
     * @param parentId the ID of the parent
     * @return list of vaccinations for infants of the parent
     */
    @Query("SELECT v FROM Vaccination v WHERE v.infant.mother.id = :parentId")
    List<Vaccination> findByParentId(@Param("parentId") UUID parentId);
}