package rw.ac.auca.kuzahealth.core.visitnote.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import rw.ac.auca.kuzahealth.core.caseload.CaseloadGuard;
import rw.ac.auca.kuzahealth.core.exception.BadRequestException;
import rw.ac.auca.kuzahealth.core.visit.entity.Visit;
import rw.ac.auca.kuzahealth.core.visit.repository.VisitRepository;
import rw.ac.auca.kuzahealth.core.visitnote.dto.VisitNoteRequest;
import rw.ac.auca.kuzahealth.core.visitnote.entity.VisitNote;
import rw.ac.auca.kuzahealth.core.visitnote.repository.VisitNoteRepository;
import rw.ac.auca.kuzahealth.utils.SoftDeleter;

@Service
@AllArgsConstructor
public class VisitNoteService {

    private final VisitNoteRepository visitNoteRepository;
    private final VisitRepository visitRepository;
    private final SoftDeleter softDeleter;
    private final CaseloadGuard caseloadGuard;

    @Transactional
    public VisitNote createVisitNote(VisitNoteRequest request) {
        if (request.getVisitId() == null) {
            throw new BadRequestException("visitId is required");
        }
        Visit visit = visitRepository.findById(request.getVisitId())
                .orElseThrow(() -> new EntityNotFoundException("Visit not found with ID: " + request.getVisitId()));
        caseloadGuard.check(visit.getParent());

        VisitNote visitNote = new VisitNote();
        visitNote.setObservation(request.getObservation());
        visitNote.setVitalSigns(request.getVitalSigns());
        visitNote.setRecommendations(request.getRecommendations());
        visitNote.setAttachments(request.getAttachments());
        visitNote.setVisit(visit);
        return visitNoteRepository.save(visitNote);
    }

    @Transactional(readOnly = true)
    public List<VisitNote> getAllVisitNotes() {
        return caseloadGuard.filter(visitNoteRepository.findAll(), note -> note.getVisit().getParent());
    }

    @Transactional(readOnly = true)
    public Optional<VisitNote> getVisitNoteById(UUID id) {
        Optional<VisitNote> note = visitNoteRepository.findById(id);
        note.ifPresent(found -> caseloadGuard.check(found.getVisit().getParent()));
        return note;
    }

    @Transactional
    public void deleteVisitNoteById(UUID id) {
        VisitNote note = getVisitNoteById(id)
                .orElseThrow(() -> new EntityNotFoundException("Visit note not found with ID: " + id));
        softDeleter.delete(VisitNote.class, note.getId());
    }
}
