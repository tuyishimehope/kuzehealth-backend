package rw.ac.auca.kuzahealth.core.pregnancyrecord.entity;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import rw.ac.auca.kuzahealth.core.parent.entity.Parent;
import org.hibernate.annotations.SQLRestriction;
import rw.ac.auca.kuzahealth.utils.SoftDeletableEntity;

@Getter
@Setter
@Table(name="pregnancy_record")
@Entity
@SQLRestriction(SoftDeletableEntity.NOT_DELETED)
public class PregnancyRecord extends SoftDeletableEntity {

    private String gravity;

    private int parity;

    // The JSON names stay snake_case because existing clients read them that way.
    @JsonProperty("last_menstrual_period")
    @Column(name = "last_menstrual_period")
    private Date lastMenstrualPeriod;

    @JsonProperty("medical_history")
    @Column(name = "medical_history")
    private String medicalHistory;

    @JsonProperty("pregnancy_complications")
    @Column(name = "pregnancy_complications")
    private String pregnancyComplications;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    @JsonIgnore
    private Parent parent;

    @JsonProperty("parentId")
    public UUID getParentId() {
        return parent != null ? parent.getId() : null;
    }
}
