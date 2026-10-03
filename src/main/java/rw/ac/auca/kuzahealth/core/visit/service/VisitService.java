package rw.ac.auca.kuzahealth.core.visit.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.core.caseload.CaseloadGuard;
import rw.ac.auca.kuzahealth.core.exception.BadRequestException;
import rw.ac.auca.kuzahealth.core.exception.ResourceNotFoundException;
import rw.ac.auca.kuzahealth.core.healthworker.entity.HealthWorker;
import rw.ac.auca.kuzahealth.core.healthworker.repository.HealthWorkerRepository;
import rw.ac.auca.kuzahealth.core.parent.entity.Parent;
import rw.ac.auca.kuzahealth.core.parent.repository.ParentRepository;
import rw.ac.auca.kuzahealth.core.visit.dto.VisitRequest;
import rw.ac.auca.kuzahealth.core.visit.entity.Visit;
import rw.ac.auca.kuzahealth.core.visit.enums.VisitStatus;
import rw.ac.auca.kuzahealth.core.visit.repository.VisitRepository;
import rw.ac.auca.kuzahealth.core.visitnote.entity.VisitNote;
import rw.ac.auca.kuzahealth.core.notification.NotificationService;
import rw.ac.auca.kuzahealth.core.notification.SmsPurpose;
import rw.ac.auca.kuzahealth.utils.SoftDeleter;

@Service
@RequiredArgsConstructor
public class VisitService {
    private final VisitRepository visitRepository;
    private final HealthWorkerRepository healthWorkerRepository;
    private final ParentRepository parentRepository;
    private final NotificationService notificationService;
    private final SoftDeleter softDeleter;
    private final CaseloadGuard caseloadGuard;

    @Transactional
    public Visit createVisit(VisitRequest request) {
        require(request.getScheduledTime(), "scheduledTime");
        require(request.getVisitType(), "visitType");
        require(request.getLocation(), "location");
        require(request.getModeOfCommunication(), "modeOfCommunication");
        require(request.getHealthWorkerId(), "healthWorkerId");
        require(request.getParentId(), "parent_id");

        Visit visit = new Visit();
        visit.setScheduledTime(request.getScheduledTime());
        visit.setActualStartTime(request.getActualStartTime());
        visit.setActualEndTime(request.getActualEndTime());
        visit.setVisitType(request.getVisitType());
        visit.setLocation(request.getLocation());
        visit.setModeOfCommunication(request.getModeOfCommunication());
        visit.setSummary(request.getSummary());
        if (request.getStatus() != null) {
            visit.setStatus(request.getStatus());
        }
        visit.setHealthWorker(findHealthWorker(request.getHealthWorkerId()));
        Parent parent = findParent(request.getParentId());
        visit.setParent(parent);

        if (request.getVisitNotes() != null) {
            List<VisitNote> notes = request.getVisitNotes().stream().map(noteReq -> {
                VisitNote note = new VisitNote();
                note.setObservation(noteReq.getObservation());
                note.setVitalSigns(noteReq.getVitalSigns());
                note.setRecommendations(noteReq.getRecommendations());
                note.setAttachments(noteReq.getAttachments());
                note.setVisit(visit); // Set back-reference
                return note;
            }).toList();
            visit.setVisitNotes(notes);
        }

        Visit saved = visitRepository.save(visit);

        notificationService.notifyParent(parent, SmsPurpose.VISIT_SCHEDULED, "visit.scheduled",
                parent.getFirstName(), saved.getVisitType(),
                notificationService.formatDate(saved.getScheduledTime()),
                notificationService.formatTime(saved.getScheduledTime()));
        return saved;
    }

    @Transactional(readOnly = true)
    public Optional<Visit> getVisitById(UUID id) {
        Optional<Visit> visit = visitRepository.findById(id);
        visit.ifPresent(found -> caseloadGuard.check(found.getParent()));
        return visit;
    }

    @Transactional(readOnly = true)
    public List<Visit> getVisitByParentId(UUID id) {
        return caseloadGuard.filter(visitRepository.findByParent_Id(id), Visit::getParent);
    }

    @Transactional(readOnly = true)
    public List<Visit> getAllVisits() {
        return caseloadGuard.filter(visitRepository.findAll(), Visit::getParent);
    }

    @Transactional(readOnly = true)
    public Page<Visit> search(VisitStatus status, UUID healthWorkerId, UUID parentId, Date from, Date to,
            Pageable pageable) {
        Optional<UUID> restrictedTo = caseloadGuard.restrictedTo();
        Specification<Visit> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            restrictedTo.ifPresent(workerId -> predicates.add(
                    cb.equal(root.get("parent").get("assignedHealthWorker").get("id"), workerId)));
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (healthWorkerId != null) {
                predicates.add(cb.equal(root.get("healthWorker").get("id"), healthWorkerId));
            }
            if (parentId != null) {
                predicates.add(cb.equal(root.get("parent").get("id"), parentId));
            }
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("scheduledTime"), from));
            }
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("scheduledTime"), to));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
        return visitRepository.findAll(spec, pageable);
    }

    /** Scheduled visits in the next {@code days} days, soonest first. */
    @Transactional(readOnly = true)
    public List<Visit> upcoming(UUID healthWorkerId, int days) {
        Date now = new Date();
        Date until = new Date(now.getTime() + TimeUnit.DAYS.toMillis(Math.max(days, 1)));
        return caseloadGuard.filter(visitRepository.findAll(forWorker(healthWorkerId, (root, query, cb) -> cb.and(
                cb.equal(root.get("status"), VisitStatus.SCHEDULED),
                cb.between(root.get("scheduledTime"), now, until))), Sort.by("scheduledTime")), Visit::getParent);
    }

    /**
     * Visits to follow up: marked as missed, or still scheduled although their time
     * passed more than {@code graceHours} ago without the visit being started.
     */
    @Transactional(readOnly = true)
    public List<Visit> missed(UUID healthWorkerId, long graceHours) {
        Date cutoff = new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(graceHours));
        return caseloadGuard.filter(visitRepository.findAll(forWorker(healthWorkerId, (root, query, cb) -> cb.or(
                cb.equal(root.get("status"), VisitStatus.MISSED),
                cb.and(cb.equal(root.get("status"), VisitStatus.SCHEDULED),
                        cb.isNull(root.get("actualStartTime")),
                        cb.lessThan(root.get("scheduledTime"), cutoff)))),
                Sort.by(Sort.Direction.DESC, "scheduledTime")), Visit::getParent);
    }

    private static Specification<Visit> forWorker(UUID healthWorkerId, Specification<Visit> spec) {
        if (healthWorkerId == null) {
            return spec;
        }
        return spec.and((root, query, cb) -> cb.equal(root.get("healthWorker").get("id"), healthWorkerId));
    }

    @Transactional
    public void deleteVisit(UUID id) {
        softDeleter.deleteVisit(requireVisit(id).getId());
    }

    @Transactional
    public Visit updateVisit(UUID id, VisitRequest request) {
        Visit visit = requireVisit(id);

        if (request.getScheduledTime() != null) {
            visit.setScheduledTime(request.getScheduledTime());
        }
        if (request.getActualStartTime() != null) {
            visit.setActualStartTime(request.getActualStartTime());
        }
        if (request.getActualEndTime() != null) {
            visit.setActualEndTime(request.getActualEndTime());
        }
        if (request.getVisitType() != null) {
            visit.setVisitType(request.getVisitType());
        }
        if (request.getLocation() != null) {
            visit.setLocation(request.getLocation());
        }
        if (request.getModeOfCommunication() != null) {
            visit.setModeOfCommunication(request.getModeOfCommunication());
        }
        if (request.getSummary() != null) {
            visit.setSummary(request.getSummary());
        }
        if (request.getStatus() != null) {
            visit.setStatus(request.getStatus());
        }
        if (request.getHealthWorkerId() != null) {
            visit.setHealthWorker(findHealthWorker(request.getHealthWorkerId()));
        }
        if (request.getParentId() != null) {
            visit.setParent(findParent(request.getParentId()));
        }
        // Visit notes are managed through the visit-notes endpoints.

        return visitRepository.save(visit);
    }

    public Visit requireVisit(UUID id) {
        Visit visit = visitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found"));
        caseloadGuard.check(visit.getParent());
        return visit;
    }

    private HealthWorker findHealthWorker(UUID id) {
        return healthWorkerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HealthWorker not found"));
    }

    private Parent findParent(UUID id) {
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found"));
        caseloadGuard.check(parent);
        return parent;
    }

    private static void require(Object value, String field) {
        if (value == null || (value instanceof String text && text.isBlank())) {
            throw new BadRequestException(field + " is required");
        }
    }
}
