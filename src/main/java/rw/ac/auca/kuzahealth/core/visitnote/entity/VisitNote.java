package rw.ac.auca.kuzahealth.core.visitnote.entity;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import rw.ac.auca.kuzahealth.core.visit.entity.Visit;
import rw.ac.auca.kuzahealth.utils.BaseEntity;

@Entity
@Getter
@Setter
@Table(name="visit_note")
public class VisitNote extends BaseEntity {

    private String observation;

    @Column(columnDefinition = "TEXT")
    private String vitalSigns;

    @Column(columnDefinition = "TEXT")
    private String recommendations;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> attachments;

    @ManyToOne
    @JoinColumn(name = "visit_id", nullable = false)
    @JsonBackReference
    private Visit visit;

    @JsonProperty("visitId")
    public UUID getVisitId() {
        return visit != null ? visit.getId() : null;
    }

}
