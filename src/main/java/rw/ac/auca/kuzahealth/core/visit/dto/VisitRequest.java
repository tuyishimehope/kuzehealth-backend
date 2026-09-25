package rw.ac.auca.kuzahealth.core.visit.dto;


import lombok.Data;
import rw.ac.auca.kuzahealth.core.visitnote.dto.VisitNoteRequest;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class VisitRequest {
    private Date scheduledTime;
    private Date actualStartTime;
    private Date actualEndTime;
    private String visitType;
    private String location;
    private String modeOfCommunication;
    private String summary;
    private UUID healthWorkerId;
    private UUID parent_id;
    private List<VisitNoteRequest> visitNotes;
}
