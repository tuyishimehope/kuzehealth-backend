package rw.ac.auca.kuzahealth.sms.model;

public class SmsResponse {
    private boolean success;
    private String message;
    private Object data;
    private String providerResponse;
    
    // Constructors
    public SmsResponse() {}
    public SmsResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    // Getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }

    /** Raw body returned by the SMS provider. Kept for the SMS log, not sent to API clients. */
    @com.fasterxml.jackson.annotation.JsonIgnore
    public String getProviderResponse() { return providerResponse; }
    public void setProviderResponse(String providerResponse) { this.providerResponse = providerResponse; }
} 