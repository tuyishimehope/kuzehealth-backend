package rw.ac.auca.kuzahealth.core.visitnote.service;


import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rw.ac.auca.kuzahealth.core.visit.entity.Visit;
import rw.ac.auca.kuzahealth.core.visit.repository.VisitRepository;
import rw.ac.auca.kuzahealth.core.visitnote.dto.VisitNoteRequest;
import rw.ac.auca.kuzahealth.core.visitnote.entity.VisitNote;
import rw.ac.auca.kuzahealth.core.visitnote.repository.VisitNoteRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class VisitNoteService {

    private final VisitNoteRepository visitNoteRepository;
    private final VisitRepository visitRepository;

//    @Autowired
//    public VisitNoteService(VisitNoteRepository visitNoteRepository) {
//        this.visitNoteRepository = visitNoteRepository;
//    }

    public VisitNote createVisitNote(VisitNoteRequest request) {
        Visit visit = visitRepository.findById(request.getVisitId())
                .orElseThrow(() -> new EntityNotFoundException("Visit not found with ID: " + request.getVisitId()));

        VisitNote visitNote = new VisitNote();
        visitNote.setObservation(request.getObservation());
        visitNote.setVitalSigns(request.getVitalSigns());
        visitNote.setRecommendations(request.getRecommendations());
        visitNote.setAttachments(request.getAttachments());
        visitNote.setVisit(visit);
        return visitNoteRepository.save(visitNote);
    }

    public List<VisitNote> getAllVisitNotes() {
        return visitNoteRepository.findAll();
    }

    public Optional<VisitNote> getVisitNoteById(UUID id) {
        return visitNoteRepository.findById(id);
    }

    public void deleteVisitNoteById(UUID id) {
        visitNoteRepository.deleteById(id);
    }


}
