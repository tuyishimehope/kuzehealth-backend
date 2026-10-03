package rw.ac.auca.kuzahealth.core.growth;

import java.util.Date;

import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import rw.ac.auca.kuzahealth.core.infant.entity.Infant;
import rw.ac.auca.kuzahealth.utils.SoftDeletableEntity;

/**
 * Anthropometric measurements of an infant taken on one day.
 */
@Entity
@Getter
@Setter
@Table(name = "growth_measurement")
@SQLRestriction(SoftDeletableEntity.NOT_DELETED)
public class GrowthMeasurement extends SoftDeletableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "infant_id", nullable = false)
    private Infant infant;

    @Temporal(TemporalType.DATE)
    @Column(name = "measured_at", nullable = false)
    private Date measuredAt;

    @Column(name = "weight_kg")
    private Double weightKg;

    /** Recumbent length or standing height. */
    @Column(name = "height_cm")
    private Double heightCm;

    @Column(name = "head_circumference_cm")
    private Double headCircumferenceCm;

    /** Mid-upper arm circumference. */
    @Column(name = "muac_cm")
    private Double muacCm;

    @Column(length = 500)
    private String notes;

    /** Email of the staff member who recorded the measurement. */
    @Column(name = "recorded_by")
    private String recordedBy;
}
