package rw.ac.auca.kuzahealth.sms.model;

public class BulkSmsRecipient {
    private String phonenumber;
    private String name;
    
    // Constructors
    public BulkSmsRecipient() {}
    public BulkSmsRecipient(String phonenumber, String name) {
        this.phonenumber = phonenumber;
        this.name = name;
    }
    
    // Getters and setters
    public String getPhonenumber() { return phonenumber; }
    public void setPhonenumber(String phonenumber) { this.phonenumber = phonenumber; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
} 