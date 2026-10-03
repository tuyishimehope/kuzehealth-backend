package rw.ac.auca.kuzahealth.core.healthworker.entity;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

    // The JSON names stay snake_case because existing clients read them that way.
    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private String qualification;

    @JsonProperty("service_area")
    private String serviceArea;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "healthWorker")
    @JsonIgnore
    private List<Visit> visits;

    @JsonProperty("user_id")
    public UUID getUserId() {
        return user != null ? user.getId() : null;
    }

    @JsonIgnore
    public String getFullName() {
        return ((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "")).trim();
    }
}
