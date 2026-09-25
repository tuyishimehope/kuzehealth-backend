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
import rw.ac.auca.kuzahealth.utils.BaseEntity;

/**
 * Entity representing a vaccination record for an infant
 */
@Entity
@Table(name = "vaccination")
@Getter
@Setter
public class Vaccination extends BaseEntity {

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

    @ManyToOne
    @JoinColumn(name = "infant_id", nullable = false)
    @JsonIgnore
    private Infant infant;

    @ManyToOne
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