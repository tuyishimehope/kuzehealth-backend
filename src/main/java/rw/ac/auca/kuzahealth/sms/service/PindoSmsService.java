package rw.ac.auca.kuzahealth.sms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;
import rw.ac.auca.kuzahealth.sms.config.PindoProperties;
import rw.ac.auca.kuzahealth.sms.model.BulkSmsRecipient;
import rw.ac.auca.kuzahealth.sms.model.BulkSmsRequest;
import rw.ac.auca.kuzahealth.sms.model.SingleSmsRequest;
import rw.ac.auca.kuzahealth.sms.model.SmsResponse;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PindoSmsService {
    
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final PindoProperties pindoProperties;
    
    public PindoSmsService(PindoProperties pindoProperties) {
        this.pindoProperties = pindoProperties;
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Send single SMS
     */
    public SmsResponse sendSingleSms(String phoneNumber, String message, String sender) {
        try {
            SingleSmsRequest smsRequest = new SingleSmsRequest(phoneNumber, message, sender);
            String jsonBody = objectMapper.writeValueAsString(smsRequest);
            
            RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), 
                jsonBody
            );
            
            Request request = new Request.Builder()
                .url(pindoProperties.getApiUrl())
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + pindoProperties.getToken())
                .build();
            log.info("request: {}", request);
            
            try (Response response = httpClient.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "";
                
                if (response.isSuccessful()) {
                    log.info("SMS sent successfully to {}", phoneNumber);
                    return new SmsResponse(true, "SMS sent successfully");
                } else {
                    log.error("Failed to send SMS to {}. Response: {}", phoneNumber, responseBody);
                    return new SmsResponse(false, "Failed to send SMS: " + responseBody);
                }
            }
        } catch (Exception e) {
            log.error("Error sending SMS to {}: {}", phoneNumber, e.getMessage(), e);
            return new SmsResponse(false, "Error sending SMS: " + e.getMessage());
        }
    }
    
    /**
     * Send bulk SMS
     */
    public SmsResponse sendBulkSms(List<BulkSmsRecipient> recipients, String message, String sender) {
        try {
            BulkSmsRequest bulkRequest = new BulkSmsRequest(recipients, message, sender);
            String jsonBody = objectMapper.writeValueAsString(bulkRequest);
            
            RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), 
                jsonBody
            );
            
            Request request = new Request.Builder()
                .url(pindoProperties.getBulkApiUrl())
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + pindoProperties.getToken())
                .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "";
                
                if (response.isSuccessful()) {
                    log.info("Bulk SMS sent successfully to {} recipients", recipients.size());
                    return new SmsResponse(true, "Bulk SMS sent successfully");
                } else {
                    log.error("Failed to send bulk SMS. Response: {}", responseBody);
                    return new SmsResponse(false, "Failed to send bulk SMS: " + responseBody);
                }
            }
        } catch (Exception e) {
            log.error("Error sending bulk SMS: {}", e.getMessage(), e);
            return new SmsResponse(false, "Error sending bulk SMS: " + e.getMessage());
        }
    }
    
    /**
     * Convenience method for single SMS with default sender
     */
    public SmsResponse sendSms(String phoneNumber, String message) {
        return sendSingleSms(phoneNumber, message, "Pindo");
    }
    
    /**
     * Convenience method for bulk SMS with default sender
     */
    public SmsResponse sendBulkSms(List<BulkSmsRecipient> recipients, String message) {
        return sendBulkSms(recipients, message, "Pindo");
    }
} 