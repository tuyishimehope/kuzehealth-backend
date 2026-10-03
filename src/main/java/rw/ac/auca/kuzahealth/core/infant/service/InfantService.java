package rw.ac.auca.kuzahealth.core.infant.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import rw.ac.auca.kuzahealth.core.infant.dto.InfantRequest;
import rw.ac.auca.kuzahealth.core.infant.entity.Infant;
import rw.ac.auca.kuzahealth.core.parent.entity.Parent;

/**
 * Service interface for managing Infant entities
 */
public interface InfantService {
    
    /**
     * Register an infant for the mother named in the request
     *
     * @return the saved infant
     */
    Infant createInfant(InfantRequest request);

    /**
     * Replace an infant's details
     *
     * @return the saved infant
     */
    Infant updateInfant(UUID id, InfantRequest request);

    /**
     * Paged search by name, optionally limited to one mother
     */
    Page<Infant> search(String q, UUID motherId, Pageable pageable);
    
    /**
     * Find an infant by ID
     * 
     * @param id the ID of the infant
     * @return the infant if found, or null
     */
    Infant findById(UUID id);
    
    /**
     * Find all infants
     * 
     * @return list of all infants
     */
    List<Infant> findAll();
    
    /**
     * Find all infants by mother
     * 
     * @param mother the parent who is the mother
     * @return list of infants belonging to the specified mother
     */
    List<Infant> findByMother(Parent mother);
    
    /**
     * Find all infants by mother's ID
     * 
     * @param motherId the ID of the mother
     * @return list of infants belonging to the mother with the specified ID
     */
    List<Infant> findByMotherId(UUID motherId);
    
    /**
     * Delete an infant by ID
     * 
     * @param id the ID of the infant to delete
     */
    void deleteById(UUID id);
}