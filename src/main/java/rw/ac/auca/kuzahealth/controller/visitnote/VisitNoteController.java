package rw.ac.auca.kuzahealth.controller.visitnote;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rw.ac.auca.kuzahealth.core.visitnote.dto.VisitNoteRequest;
import rw.ac.auca.kuzahealth.core.visitnote.entity.VisitNote;
import rw.ac.auca.kuzahealth.core.visitnote.service.VisitNoteService;

@RestController
@RequestMapping("/api/visit-notes")
public class VisitNoteController {

    private final VisitNoteService visitNoteService;

    @Autowired
    public VisitNoteController(VisitNoteService visitNoteService) {
        this.visitNoteService = visitNoteService;
    }

    @PostMapping
    public ResponseEntity<VisitNote> createVisitNote(@RequestBody VisitNoteRequest visitNote) {
        VisitNote createdVisitNote = visitNoteService.createVisitNote(visitNote);
        return new ResponseEntity<>(createdVisitNote, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<VisitNote>> getAllVisitNotes() {
        List<VisitNote> visitNotes = visitNoteService.getAllVisitNotes();
        return new ResponseEntity<>(visitNotes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VisitNote> getVisitNoteById(@PathVariable UUID id) {
        Optional<VisitNote> visitNote = visitNoteService.getVisitNoteById(id);
        return visitNote.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVisitNoteById(@PathVariable UUID id) {
        visitNoteService.deleteVisitNoteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
