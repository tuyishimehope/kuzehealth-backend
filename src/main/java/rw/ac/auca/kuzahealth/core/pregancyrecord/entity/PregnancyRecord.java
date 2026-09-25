package rw.ac.auca.kuzahealth.core.pregancyrecord.entity;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import rw.ac.auca.kuzahealth.core.parent.entity.Parent;
import rw.ac.auca.kuzahealth.utils.BaseEntity;

@Getter
@Setter
@Table(name="pregnancy_record")
@Entity
public class PregnancyRecord extends BaseEntity{

    public String gravity;
    public int parity;
    public Date last_menstrual_period;
    public String medical_history;
    public String  pregnancy_complications;


    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false)
    @JsonIgnore
    private Parent parent;

    @JsonProperty("parentId")
    public UUID getParentId() {
        return parent != null ? parent.getId() : null;
    }
}
