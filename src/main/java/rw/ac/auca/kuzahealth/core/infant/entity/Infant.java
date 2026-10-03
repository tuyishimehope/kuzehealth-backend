package rw.ac.auca.kuzahealth.core.infant.entity;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import rw.ac.auca.kuzahealth.core.parent.entity.Parent;
import rw.ac.auca.kuzahealth.core.vaccination.entity.Vaccination;
import org.hibernate.annotations.SQLRestriction;
import rw.ac.auca.kuzahealth.utils.SoftDeletableEntity;

@Table(name = "infant")
@Entity
@Getter
@Setter
@SQLRestriction(SoftDeletableEntity.NOT_DELETED)
public class Infant extends SoftDeletableEntity {

    private String firstName;
    private String lastName;

    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    private String gender;

    @Column(name = "birth_weight_grams")
    private Double birthWeight;

    @Column(name = "birth_height_cm")
    private Double birthHeight;

    private String bloodGroup;

    @Column(name = "special_conditions")
    private String specialConditions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mother_id", nullable = false)
    @JsonIgnore
    private Parent mother;

    @JsonProperty("motherId")
    public UUID getMotherId() {
        return mother != null ? mother.getId() : null;
    }

    @OneToMany(mappedBy = "infant")
    @JsonIgnore
    private List<Vaccination> vaccinations;
}
