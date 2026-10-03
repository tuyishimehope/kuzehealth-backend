package rw.ac.auca.kuzahealth.core.healthworker.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class HealthWorkerRequest {

    @JsonProperty("first_name")
    @JsonAlias("firstName")
    private String firstName;

    @JsonProperty("last_name")
    @JsonAlias("lastName")
    private String lastName;

    @Email
    private String email;

    @JsonProperty("phone_number")
    @JsonAlias("phoneNumber")
    private String phoneNumber;

    private String qualification;

    @JsonProperty("service_area")
    @JsonAlias("serviceArea")
    private String serviceArea;
}
