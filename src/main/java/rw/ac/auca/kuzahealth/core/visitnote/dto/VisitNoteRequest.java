package rw.ac.auca.kuzahealth.core.visitnote.dto;


import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class VisitNoteRequest {
    private String observation;
    private String vitalSigns;
    private String recommendations;
    private List<String> attachments;
    private UUID visitId;
}
