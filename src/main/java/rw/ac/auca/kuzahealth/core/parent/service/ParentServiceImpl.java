package rw.ac.auca.kuzahealth.core.parent.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rw.ac.auca.kuzahealth.core.parent.dto.ParentRequest;
import rw.ac.auca.kuzahealth.core.parent.entity.Parent;
import rw.ac.auca.kuzahealth.core.parent.repository.ParentRepository;

@Service
public class ParentServiceImpl {

    private final ParentRepository parentRepository;

    @Autowired
    public ParentServiceImpl(ParentRepository parentRepository) {
        this.parentRepository = parentRepository;
    }

    public Parent registerParent(Parent request) {
        Parent parent = new Parent();
        parent.setFirstName(request.getFirstName());
        parent.setLastName(request.getLastName());
        parent.setEmail(request.getEmail());
        parent.setPhone(request.getPhone());
        parent.setExpectedDeliveryDate(request.getExpectedDeliveryDate());
        parent.setHighRisk(request.isHighRisk());
        parent.setBloodGroup(request.getBloodGroup());
        parent.setMaritalStatus(request.getMaritalStatus());
        parent.setEmergencyContactNumber(request.getEmergencyContactNumber());
        parent.setEmergencyContactFullName(request.getEmergencyContactFullName());
        parent.setEmergencyContactRelationship(request.getEmergencyContactRelationship());
        parent.setDistrict(request.getDistrict());
        parent.setSector(request.getSector());
        parent.setCell(request.getCell());
        parent.setVillage(request.getVillage());

        return parentRepository.save(parent);
    }

    public List<Parent> getAllParents() {
        return parentRepository.findAll();
    }

    public Parent getParentById(UUID id) {
        Optional<Parent> parentOptional = parentRepository.findById(id);
        return parentOptional.orElse(null);
    }

    public Parent updateParent(UUID id, Parent parent) {
        if (parentRepository.existsById(id)) {
            parent.setId(id); // Make sure to set the ID to update the correct record
            return parentRepository.save(parent);
        }
        return null; // or throw an exception
    }

    public boolean deleteParent(UUID id) {
        if (parentRepository.existsById(id)) {
            parentRepository.deleteById(id);
            return true;
        }
        return false; // or throw an exception
    }

}
