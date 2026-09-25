package rw.ac.auca.kuzahealth.sms.model;

public class SingleSmsRequest {
    private String to;
    private String text;
    private String sender;
    
    // Constructors
    public SingleSmsRequest() {}
    public SingleSmsRequest(String to, String text, String sender) {
        this.to = to;
        this.text = text;
        this.sender = sender;
    }
    
    // Getters and setters
    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
} 