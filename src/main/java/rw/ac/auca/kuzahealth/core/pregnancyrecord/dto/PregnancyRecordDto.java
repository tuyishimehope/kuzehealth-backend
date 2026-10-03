package rw.ac.auca.kuzahealth.core.pregnancyrecord.dto;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PregnancyRecordDto {
    private String gravity;
    private int parity;

    @JsonProperty("last_menstrual_period")
    @JsonAlias("lastMenstrualPeriod")
    private Date lastMenstrualPeriod;

    @JsonProperty("medical_history")
    @JsonAlias("medicalHistory")
    private String medicalHistory;

    @JsonProperty("pregnancy_complications")
    @JsonAlias("pregnancyComplications")
    private String pregnancyComplications;

    @JsonAlias("parent_id")
    private UUID parentId;

    /** Accepts the nested form {@code "parent": {"id": "..."}} as well as parentId. */
    @JsonProperty("parent")
    public void setParentReference(ParentReference parent) {
        if (parent != null && parent.getId() != null) {
            this.parentId = parent.getId();
        }
    }

    @Data
    public static class ParentReference {
        private UUID id;
    }
}
