package rw.ac.auca.kuzahealth.core.visit.dto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import rw.ac.auca.kuzahealth.core.visit.enums.VisitStatus;
import rw.ac.auca.kuzahealth.core.visitnote.dto.VisitNoteRequest;

/**
 * Used for both create and update. On update only the fields that are present change.
 */
@Data
public class VisitRequest {
    private Date scheduledTime;
    private Date actualStartTime;
    private Date actualEndTime;
    private String visitType;
    private String location;
    private String modeOfCommunication;
    private VisitStatus status;
    private String summary;
    private UUID healthWorkerId;

    @JsonProperty("parent_id")
    @JsonAlias("parentId")
    private UUID parentId;

    private List<VisitNoteRequest> visitNotes;
}
