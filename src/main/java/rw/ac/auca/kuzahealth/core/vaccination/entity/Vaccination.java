package rw.ac.auca.kuzahealth.core.vaccination.entity;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import rw.ac.auca.kuzahealth.core.healthworker.entity.HealthWorker;
import rw.ac.auca.kuzahealth.core.infant.entity.Infant;
import org.hibernate.annotations.SQLRestriction;
import rw.ac.auca.kuzahealth.utils.SoftDeletableEntity;

/**
 * Entity representing a vaccination record for an infant
 */
@Entity
@Table(name = "vaccination")
@Getter
@Setter
@SQLRestriction(SoftDeletableEntity.NOT_DELETED)
public class Vaccination extends SoftDeletableEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date administeredDate;

    @Temporal(TemporalType.DATE)
    private Date nextDueDate;

    @Column(nullable = false)
    private boolean notificationSent = false;

    private String notes;

    /** Code of the routine schedule item this dose fulfils, when it is a scheduled dose. */
    @Column(name = "schedule_code", length = 32)
    private String scheduleCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "infant_id", nullable = false)
    @JsonIgnore
    private Infant infant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_worker_id", nullable = false)
    @JsonIgnore
    private HealthWorker healthWorker;

    @JsonProperty("infantId")
    public UUID getInfantId() {
        return infant != null ? infant.getId() : null;
    }

    @JsonProperty("healthWorkerId")
    public UUID getHealthWorkerId() {
        return healthWorker != null ? healthWorker.getId() : null;
    }
}