package rw.ac.auca.kuzahealth.sms.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rw.ac.auca.kuzahealth.sms.model.BulkSmsRecipient;
import rw.ac.auca.kuzahealth.sms.model.BulkSmsRequest;
import rw.ac.auca.kuzahealth.sms.model.SingleSmsRequest;
import rw.ac.auca.kuzahealth.sms.model.SmsResponse;
import rw.ac.auca.kuzahealth.sms.service.BackendService;
import rw.ac.auca.kuzahealth.sms.service.PindoSmsService;

import java.util.List;

@RestController
@RequestMapping("/api/sms")
@Slf4j
public class SmsController {
    
    private final PindoSmsService smsService;
    private final BackendService backendService;
    
    public SmsController(PindoSmsService smsService, BackendService backendService) {
        this.smsService = smsService;
        this.backendService = backendService;
    }
    
    @PostMapping("/send")
    public ResponseEntity<SmsResponse> sendSms(@RequestBody SingleSmsRequest request) {
        SmsResponse response = smsService.sendSingleSms(
            request.getTo(), 
            request.getText(), 
            request.getSender()
        );
        
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : 
            ResponseEntity.badRequest().body(response);
    }
    
    @PostMapping("/send-bulk")
    public ResponseEntity<SmsResponse> sendBulkSms(@RequestBody BulkSmsRequest request) {
        SmsResponse response = smsService.sendBulkSms(
            request.getRecipients(), 
            request.getText(), 
            request.getSender()
        );
        
        return response.isSuccess() ? 
            ResponseEntity.ok(response) : 
            ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/send-bulk-from-backend")
    public ResponseEntity<SmsResponse> sendBulkSmsFromBackend(@RequestBody BulkSmsRequest request) {
        try {
            // Get phone numbers from backend
            List<BulkSmsRecipient> recipients = backendService.getPhoneNumbers();
            
            // Update the request with recipients from backend
            request.setRecipients(recipients);
            
            // Send the SMS
            SmsResponse response = smsService.sendBulkSms(
                request.getRecipients(),
                request.getText(),
                request.getSender()
            );
            
            return response.isSuccess() ? 
                ResponseEntity.ok(response) : 
                ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Error sending bulk SMS from backend: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                new SmsResponse(false, "Error sending bulk SMS: " + e.getMessage())
            );
        }
    }
} 