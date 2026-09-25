package rw.ac.auca.kuzahealth.sms.model;

import lombok.Data;

@Data
public class SmsRequest {
    private String to;
    private String text;
    private String sender;
} 