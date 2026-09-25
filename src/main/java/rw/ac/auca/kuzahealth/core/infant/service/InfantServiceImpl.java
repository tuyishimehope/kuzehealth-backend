package rw.ac.auca.kuzahealth.core.infant.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rw.ac.auca.kuzahealth.core.infant.entity.Infant;
import rw.ac.auca.kuzahealth.core.infant.repository.InfantRepository;
import rw.ac.auca.kuzahealth.core.parent.entity.Parent;
import rw.ac.auca.kuzahealth.core.pregancyrecord.exception.ResourceNotFoundException;

/**
 * Implementation of the InfantService interface
 */
@Service
public class InfantServiceImpl implements InfantService {

    private final InfantRepository infantRepository;

    @Autowired
    public InfantServiceImpl(InfantRepository infantRepository) {
        this.infantRepository = infantRepository;
    }

    @Override
    public Infant saveInfant(Infant infant) {
        return infantRepository.save(infant);
    }

    @Override
    public Infant findById(UUID id) {
        return infantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Infant not found with id: " + id));
    }

    @Override
    public List<Infant> findAll() {
        return infantRepository.findAll();
    }

    @Override
    public List<Infant> findByMother(Parent mother) {
        return infantRepository.findByMother(mother);
    }

    @Override
    public List<Infant> findByMotherId(UUID motherId) {
        return infantRepository.findByMother_Id(motherId);
    }

    @Override
    public void deleteById(UUID id) {
        if (!infantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Infant not found with id: " + id);
        }
        infantRepository.deleteById(id);
    }
}