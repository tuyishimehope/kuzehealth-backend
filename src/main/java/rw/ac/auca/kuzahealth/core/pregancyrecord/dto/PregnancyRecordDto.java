package rw.ac.auca.kuzahealth.core.pregancyrecord.dto;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class PregnancyRecordDto {
    private String gravity;
    private int parity;
    private Date lastMenstrualPeriod;
    private String medicalHistory;
    private String pregnancyComplications;
    private UUID parentId;

}
