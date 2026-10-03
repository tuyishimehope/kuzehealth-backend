package rw.ac.auca.kuzahealth.core.visitnote.entity;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import rw.ac.auca.kuzahealth.core.visit.entity.Visit;
import org.hibernate.annotations.SQLRestriction;
import rw.ac.auca.kuzahealth.utils.SoftDeletableEntity;

@Entity
@Getter
@Setter
@Table(name="visit_note")
@SQLRestriction(SoftDeletableEntity.NOT_DELETED)
public class VisitNote extends SoftDeletableEntity {

    private String observation;

    @Column(columnDefinition = "TEXT")
    private String vitalSigns;

    @Column(columnDefinition = "TEXT")
    private String recommendations;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> attachments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id", nullable = false)
    @JsonIgnore
    private Visit visit;

    @JsonProperty("visitId")
    public UUID getVisitId() {
        return visit != null ? visit.getId() : null;
    }

}
