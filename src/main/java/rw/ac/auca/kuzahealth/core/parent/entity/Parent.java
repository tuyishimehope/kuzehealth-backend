package rw.ac.auca.kuzahealth.core.parent.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import rw.ac.auca.kuzahealth.core.infant.entity.Infant;
import rw.ac.auca.kuzahealth.core.pregancyrecord.entity.PregnancyRecord;
import rw.ac.auca.kuzahealth.utils.BaseEntity;

@Table(name = "parent")
@Getter
@Setter
@Entity
public class Parent extends BaseEntity {

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

    @OneToMany
    @JsonIgnore
    private List<PregnancyRecord> pregnancyRecord;

    @OneToMany(mappedBy = "mother")
    @JsonIgnore
    private List<Infant> infants;
}
