package rw.ac.auca.kuzahealth.core.infant.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rw.ac.auca.kuzahealth.core.infant.entity.Infant;
import rw.ac.auca.kuzahealth.core.parent.entity.Parent;

@Repository
public interface InfantRepository extends JpaRepository<Infant, UUID> {
    
    /**
     * Find all infants by their mother
     * 
     * @param mother the parent who is the mother of the infants
     * @return list of infants belonging to the specified mother
     */
    List<Infant> findByMother(Parent mother);

    /**
     * Find all infants by mother's ID
     * 
     * @param motherId the UUID of the mother
     * @return list of infants belonging to the mother with the specified ID
     */
    List<Infant> findByMother_Id(UUID motherId);
}