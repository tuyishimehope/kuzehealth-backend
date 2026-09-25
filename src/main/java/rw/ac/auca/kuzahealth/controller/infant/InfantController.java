package rw.ac.auca.kuzahealth.controller.infant;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rw.ac.auca.kuzahealth.core.infant.entity.Infant;
import rw.ac.auca.kuzahealth.core.infant.dto.InfantRequest;
import rw.ac.auca.kuzahealth.core.infant.service.InfantService;
import rw.ac.auca.kuzahealth.core.parent.entity.Parent;
import rw.ac.auca.kuzahealth.core.parent.service.ParentServiceImpl;
import rw.ac.auca.kuzahealth.utils.MessageResponse;

@RestController
@RequestMapping("/api/infants")
public class InfantController {

    private final InfantService infantService;
    private final ParentServiceImpl parentService;

    @Autowired
    public InfantController(InfantService infantService, ParentServiceImpl parentService) {
        this.infantService = infantService;
        this.parentService = parentService;
    }

    @PostMapping
    public ResponseEntity<Infant> createInfant(@RequestBody InfantRequest request) {
        Parent mother = parentService.getParentById(request.getMotherId());
        if (mother == null) {
            throw new RuntimeException("Mother not found with id: " + request.getMotherId());
        }

        Infant infant = new Infant();
        infant.setFirstName(request.getFirstName());
        infant.setLastName(request.getLastName());
        infant.setDateOfBirth(request.getDateOfBirth());
        infant.setGender(request.getGender());
        infant.setBirthWeight(request.getBirthWeight());
        infant.setBirthHeight(request.getBirthHeight());
        infant.setBloodGroup(request.getBloodGroup());
        infant.setSpecialConditions(request.getSpecialConditions());
        infant.setMother(mother);

        Infant savedInfant = infantService.saveInfant(infant);
        return new ResponseEntity<>(savedInfant, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Infant>> getAllInfants() {
        List<Infant> infants = infantService.findAll();
        return new ResponseEntity<>(infants, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Infant> getInfantById(@PathVariable UUID id) {
        Infant infant = infantService.findById(id);
        return new ResponseEntity<>(infant, HttpStatus.OK);
    }

    @GetMapping("/mother/{motherId}")
    public ResponseEntity<List<Infant>> getInfantsByMotherId(@PathVariable UUID motherId) {
        List<Infant> infants = infantService.findByMotherId(motherId);
        return new ResponseEntity<>(infants, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Infant> updateInfant(@PathVariable UUID id, @RequestBody InfantRequest request) {
        Infant existingInfant = infantService.findById(id);
        Parent mother = parentService.getParentById(request.getMotherId());
        if (mother == null) {
            throw new RuntimeException("Mother not found with id: " + request.getMotherId());
        }

        existingInfant.setFirstName(request.getFirstName());
        existingInfant.setLastName(request.getLastName());
        existingInfant.setDateOfBirth(request.getDateOfBirth());
        existingInfant.setGender(request.getGender());
        existingInfant.setBirthWeight(request.getBirthWeight());
        existingInfant.setBirthHeight(request.getBirthHeight());
        existingInfant.setBloodGroup(request.getBloodGroup());
        existingInfant.setSpecialConditions(request.getSpecialConditions());
        existingInfant.setMother(mother);

        Infant updatedInfant = infantService.saveInfant(existingInfant);
        return new ResponseEntity<>(updatedInfant, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteInfant(@PathVariable UUID id) {
        infantService.deleteById(id);
        return new ResponseEntity<>(new MessageResponse("Infant deleted successfully", HttpStatus.OK), HttpStatus.OK);
    }
}
