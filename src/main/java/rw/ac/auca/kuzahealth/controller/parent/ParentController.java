package rw.ac.auca.kuzahealth.controller.parent;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.core.notification.NotificationService;
import rw.ac.auca.kuzahealth.core.notification.SmsLog;
import rw.ac.auca.kuzahealth.core.parent.consent.ConsentRecord;
import rw.ac.auca.kuzahealth.core.parent.consent.ConsentRequest;
import rw.ac.auca.kuzahealth.core.parent.dto.ParentRequest;
import rw.ac.auca.kuzahealth.core.parent.entity.Parent;
import rw.ac.auca.kuzahealth.core.parent.service.ParentServiceImpl;
import rw.ac.auca.kuzahealth.security.CustomUserDetails;
import rw.ac.auca.kuzahealth.utils.MessageResponse;
import rw.ac.auca.kuzahealth.utils.paging.PageRequests;
import rw.ac.auca.kuzahealth.utils.paging.PageResponse;

@RestController
@RequestMapping({ "/api/parents", "/api/v1/parents" })
@RequiredArgsConstructor
public class ParentController {

    private final ParentServiceImpl parentService;
    private final NotificationService notificationService;

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> registerParent(@RequestBody @Valid ParentRequest parentRequest,
            @AuthenticationPrincipal CustomUserDetails caller) {
        parentService.registerParent(parentRequest, caller.getEmail());
        return new ResponseEntity<>(
                new MessageResponse("Parent registered successfully.", HttpStatus.CREATED),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<Parent>> getAllParents() {
        List<Parent> parents = parentService.getAllParents();
        return new ResponseEntity<>(parents, HttpStatus.OK);
    }

    /** Paged, filterable alternative to the full list. */
    @GetMapping("/search")
    public PageResponse<Parent> search(@RequestParam(required = false) String q,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) Boolean highRisk,
            @RequestParam(required = false) UUID assignedHealthWorkerId,
            @RequestParam(required = false) Boolean unassigned,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort) {
        return PageResponse.of(parentService.search(q, district, highRisk, assignedHealthWorkerId, unassigned,
                PageRequests.of(page, size, sort)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Parent> getParentById(@PathVariable UUID id) {
        Parent parent = parentService.getParentById(id);
        if (parent != null) {
            return new ResponseEntity<>(parent, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateParent(@PathVariable UUID id,
            @RequestBody @Valid ParentRequest parent, @AuthenticationPrincipal CustomUserDetails caller) {
        Parent updatedParent = parentService.updateParent(id, parent, caller.getEmail());
        if (updatedParent != null) {
            MessageResponse response = new MessageResponse("Parent updated successfully.", HttpStatus.OK);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new MessageResponse("Parent not found.", HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteParent(@PathVariable UUID id) {
        boolean isDeleted = parentService.deleteParent(id);
        if (isDeleted) {
            MessageResponse response = new MessageResponse("Parent deleted successfully.", HttpStatus.NO_CONTENT);
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(new MessageResponse("Parent not found.", HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
        }
    }

    /** Assigns the parent to a health worker's caseload; a null healthWorkerId unassigns her. */
    @PutMapping("/{id}/assignment")
    @PreAuthorize("hasRole('ADMIN')")
    public Parent assign(@PathVariable UUID id, @RequestBody AssignmentRequest request) {
        return parentService.assign(id, request.healthWorkerId());
    }

    public record AssignmentRequest(UUID healthWorkerId) {
    }

    /** Records that the parent gave or withdrew consent. Withdrawing SMS consent stops all messages to her. */
    @PutMapping("/{id}/consent")
    public ConsentRecord recordConsent(@PathVariable UUID id, @RequestBody @Valid ConsentRequest request,
            @AuthenticationPrincipal CustomUserDetails caller) {
        return parentService.recordConsent(id, request.getType(), request.getGranted(), request.getNote(),
                caller.getEmail());
    }

    /** Consent history, newest first. */
    @GetMapping("/{id}/consents")
    public List<ConsentRecord> getConsents(@PathVariable UUID id) {
        return parentService.getConsents(id);
    }

    /** Messages sent to this parent, newest first. */
    @GetMapping("/{id}/sms-logs")
    public PageResponse<SmsLog> getSmsLogs(@PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        parentService.requireParent(id);
        return PageResponse.of(notificationService.search(null, null, id, PageRequests.of(page, size, null)));
    }
}
