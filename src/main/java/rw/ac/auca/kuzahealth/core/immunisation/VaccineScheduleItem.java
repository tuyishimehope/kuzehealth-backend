package rw.ac.auca.kuzahealth.core.immunisation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import rw.ac.auca.kuzahealth.utils.BaseEntity;

/**
 * One dose in the routine immunisation schedule: which vaccine, and at what age it is due.
 */
@Entity
@Getter
@Setter
@Table(name = "vaccine_schedule_item")
public class VaccineScheduleItem extends BaseEntity {

    /** Short stable identifier, e.g. PENTA2. */
    @Column(nullable = false, unique = true, length = 32)
    private String code;

    @Column(name = "vaccine_name", nullable = false)
    private String vaccineName;

    @Column(name = "dose_number", nullable = false)
    private int doseNumber;

    /** Age in days at which the dose is due. */
    @Column(name = "due_age_days", nullable = false)
    private int dueAgeDays;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private boolean active = true;
}
