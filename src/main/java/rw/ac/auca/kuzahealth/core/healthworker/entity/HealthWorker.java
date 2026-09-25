package rw.ac.auca.kuzahealth.core.healthworker.entity;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import rw.ac.auca.kuzahealth.core.user.entity.User;
import rw.ac.auca.kuzahealth.core.visit.entity.Visit;
import rw.ac.auca.kuzahealth.utils.BaseEntity;

@Entity
@Getter
@Setter
@Table(name = "health_worker")
public class HealthWorker extends BaseEntity {

    public String first_name;
    public String last_name;
    @Column(unique = true, nullable = false)
    private String email;
    public String phone_number;
    public String qualification;
    public String service_area;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "healthWorker")
    @JsonManagedReference
    @JsonIgnore
    private List<Visit> visits;

    @JsonProperty("user_id")
    public UUID getParentId() {
        return user != null ? user.getId() : null;
    }

}
