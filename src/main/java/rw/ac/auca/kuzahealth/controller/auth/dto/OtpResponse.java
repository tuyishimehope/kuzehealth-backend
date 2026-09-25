package rw.ac.auca.kuzahealth.controller.auth.dto;

public class OtpResponse {
    private String status; // "SUCCESS" or "ERROR"
    private String message;

    // Constructors
    public OtpResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    // Getters and setters
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public void setStatus(String status) { this.status = status; }
    public void setMessage(String message) { this.message = message; }

}
