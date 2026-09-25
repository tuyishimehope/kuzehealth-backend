package rw.ac.auca.kuzahealth.sms.model;

import java.util.List;

public class BulkSmsRequest {
    private List<BulkSmsRecipient> recipients;
    private String text;
    private String sender;
    
    // Constructors
    public BulkSmsRequest() {}
    public BulkSmsRequest(List<BulkSmsRecipient> recipients, String text, String sender) {
        this.recipients = recipients;
        this.text = text;
        this.sender = sender;
    }
    
    // Getters and setters
    public List<BulkSmsRecipient> getRecipients() { return recipients; }
    public void setRecipients(List<BulkSmsRecipient> recipients) { this.recipients = recipients; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
} 