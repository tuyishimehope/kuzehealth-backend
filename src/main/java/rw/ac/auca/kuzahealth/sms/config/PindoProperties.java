package rw.ac.auca.kuzahealth.sms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "pindo")
public class PindoProperties {
    private String apiUrl = "https://api.pindo.io/v1/sms/";
    private String bulkApiUrl = "https://api.pindo.io/v1/sms/bulk";
    private String token;
    
    // Getters and setters
    public String getApiUrl() { return apiUrl; }
    public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }
    public String getBulkApiUrl() { return bulkApiUrl; }
    public void setBulkApiUrl(String bulkApiUrl) { this.bulkApiUrl = bulkApiUrl; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
} 