package rw.ac.auca.kuzahealth.core.vaccination.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import rw.ac.auca.kuzahealth.core.healthworker.entity.HealthWorker;
import rw.ac.auca.kuzahealth.core.infant.entity.Infant;
import rw.ac.auca.kuzahealth.core.vaccination.dto.VaccinationRequest;
import rw.ac.auca.kuzahealth.core.vaccination.entity.Vaccination;

/**
 * Service interface for managing Vaccination entities
 */
public interface VaccinationService {
    
    /**
     * Create a new vaccination record
     * 
     * @param request the vaccination request
     * @return the created vaccination
     */
    Vaccination createVaccination(VaccinationRequest request);
    
    /**
     * Update an existing vaccination record
     * 
     * @param id the ID of the vaccination to update
     * @param request the updated vaccination data
     * @return the updated vaccination
     */
    Vaccination updateVaccination(UUID id, VaccinationRequest request);
    
    /**
     * Find a vaccination by ID
     * 
     * @param id the ID of the vaccination
     * @return the vaccination if found
     */
    Vaccination findById(UUID id);
    
    /**
     * Find all vaccinations
     * 
     * @return list of all vaccinations
     */
    List<Vaccination> findAll();
    
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
    List<Vaccination> findByInfantId(UUID infantId);
    
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
    List<Vaccination> findByHealthWorkerId(UUID healthWorkerId);
    
    /**
     * Find all vaccinations for infants of a specific parent
     * 
     * @param parentId the ID of the parent
     * @return list of vaccinations for infants of the parent
     */
    List<Vaccination> findByParentId(UUID parentId);
    
    /**
     * Find all vaccinations with a next due date before or on the specified date
     * and notification not yet sent
     * 
     * @param date the date to check against
     * @return list of due vaccinations
     */
    List<Vaccination> findDueVaccinations(Date date);
    
    /**
     * Send notifications for due vaccinations
     * 
     * @param date the date to check against
     * @return number of notifications sent
     */
    int sendDueVaccinationNotifications(Date date);
    
    /**
     * Delete a vaccination by ID
     * 
     * @param id the ID of the vaccination to delete
     */
    void deleteById(UUID id);
}