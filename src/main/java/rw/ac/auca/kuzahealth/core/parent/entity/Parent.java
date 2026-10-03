package rw.ac.auca.kuzahealth.core.parent.entity;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import rw.ac.auca.kuzahealth.core.healthworker.entity.HealthWorker;
import rw.ac.auca.kuzahealth.core.infant.entity.Infant;
import rw.ac.auca.kuzahealth.core.parent.enums.Language;
import rw.ac.auca.kuzahealth.core.pregnancyrecord.entity.PregnancyRecord;
import org.hibernate.annotations.SQLRestriction;
import rw.ac.auca.kuzahealth.utils.SoftDeletableEntity;

@Table(name = "parent")
@Getter
@Setter
@Entity
@SQLRestriction(SoftDeletableEntity.NOT_DELETED)
public class Parent extends SoftDeletableEntity {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    @Temporal(TemporalType.DATE)
    private Date expectedDeliveryDate;

    @Column(name = "high_risk", nullable = false)
    private boolean isHighRisk = false;

    private String bloodGroup;
    private String maritalStatus;
    private String emergencyContactNumber;
    private String emergencyContactFullName;
    private String emergencyContactRelationship;
    private String district;
    private String sector;
    private String cell;
    private String village;

    /** Whether the parent agreed to receive SMS. The history is in consent_record. */
    @Column(name = "sms_consent", nullable = false)
    private boolean smsConsent = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_language", nullable = false, length = 8)
    private Language preferredLanguage = Language.EN;

    /** The health worker responsible for this parent, if one has been assigned. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_health_worker_id")
    @JsonIgnore
    private HealthWorker assignedHealthWorker;

    @JsonProperty("assignedHealthWorkerId")
    public UUID getAssignedHealthWorkerId() {
        return assignedHealthWorker != null ? assignedHealthWorker.getId() : null;
    }

    @OneToMany(mappedBy = "parent")
    @JsonIgnore
    private List<PregnancyRecord> pregnancyRecord;

    @OneToMany(mappedBy = "mother")
    @JsonIgnore
    private List<Infant> infants;

    @JsonIgnore
    public String getFullName() {
        return ((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "")).trim();
    }
}
