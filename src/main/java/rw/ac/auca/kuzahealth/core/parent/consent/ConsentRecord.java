package rw.ac.auca.kuzahealth.core.parent.consent;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import rw.ac.auca.kuzahealth.core.parent.entity.Parent;
import rw.ac.auca.kuzahealth.utils.BaseEntity;

/**
 * One consent decision by a parent. Rows are only ever added, so the table is the
 * history of what was agreed to and when.
 */
@Entity
@Getter
@Setter
@Table(name = "consent_record")
public class ConsentRecord extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parent_id", nullable = false)
    @JsonIgnore
    private Parent parent;

    @Enumerated(EnumType.STRING)
    @Column(name = "consent_type", nullable = false, length = 32)
    private ConsentType consentType;

    @Column(nullable = false)
    private boolean granted;

    /** Email of the staff member who recorded the decision. */
    @Column(name = "recorded_by")
    private String recordedBy;

    @Column(length = 500)
    private String note;

    @JsonProperty("parentId")
    public UUID getParentId() {
        return parent != null ? parent.getId() : null;
    }
}
