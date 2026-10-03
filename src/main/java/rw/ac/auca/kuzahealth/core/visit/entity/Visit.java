package rw.ac.auca.kuzahealth.core.visit.entity;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import rw.ac.auca.kuzahealth.core.healthworker.entity.HealthWorker;
import rw.ac.auca.kuzahealth.core.parent.entity.Parent;
import rw.ac.auca.kuzahealth.core.visit.enums.VisitStatus;
import rw.ac.auca.kuzahealth.core.visitnote.entity.VisitNote;
import org.hibernate.annotations.SQLRestriction;
import rw.ac.auca.kuzahealth.utils.SoftDeletableEntity;


@Entity
@Getter
@Setter
@Table(name = "visit")
@SQLRestriction(SoftDeletableEntity.NOT_DELETED)
public class Visit extends SoftDeletableEntity {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "scheduled_time", nullable = false)
    private Date scheduledTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "actual_start_time")
    private Date actualStartTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "actual_end_time")
    private Date actualEndTime;

    @Column(name = "visit_type", nullable = false)
    private String visitType;

    @Column(nullable = false)
    private String location;

    @Column(name = "mode_of_communication", nullable = false)
    private String modeOfCommunication;

    @Column(name = "status", nullable = false)
    private VisitStatus status = VisitStatus.SCHEDULED;

    @Column(columnDefinition = "TEXT")
    private String summary; // optional

    /** Whether the parent has already been reminded about this visit. */
    @Column(name = "reminder_sent", nullable = false)
    private boolean reminderSent = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private HealthWorker healthWorker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "parent_id", nullable = false)
    private Parent parent;


    @OneToMany(mappedBy = "visit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<VisitNote> visitNotes;

    @JsonProperty("healthWorkerId")
    public UUID getHealthWorkerId() {
        return healthWorker != null ? healthWorker.getId() : null;
    }

    @JsonProperty("parentId")
    public UUID getParentId() {
        return parent != null ? parent.getId() : null;
    }

    @JsonProperty("visitNoteIds")
    public List<UUID> getVisitNoteIds() {
        // Avoid triggering lazy initialization when Open-Session-In-View is disabled
        // Only compute IDs if the collection is already initialized
        if (visitNotes == null) {
            return null;
        }
        // Hibernate.isInitialized does not initialize the proxy and is safe to call
        if (org.hibernate.Hibernate.isInitialized(visitNotes)) {
            return visitNotes.stream().map(VisitNote::getId).collect(Collectors.toList());
        }
        return null;
    }

    @JsonProperty("visitId")
    public UUID getVisitId() {
        return getId();
    }
}
