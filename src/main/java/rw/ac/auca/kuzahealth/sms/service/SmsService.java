package rw.ac.auca.kuzahealth.sms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.stereotype.Service;

import rw.ac.auca.kuzahealth.sms.config.PindoProperties;
import rw.ac.auca.kuzahealth.sms.model.BulkSmsRequest;
import rw.ac.auca.kuzahealth.sms.model.SmsRequest;

import java.io.IOException;

@Service
public class SmsService {
    private static final String SINGLE_SMS_URL = "https://api.pindo.io/v1/sms/";
    private static final String BULK_SMS_URL = "https://api.pindo.io/v1/sms/bulk";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final PindoProperties pindoProperties;

    public SmsService(PindoProperties pindoProperties) {
        this.pindoProperties = pindoProperties;
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public String sendSingleSms(SmsRequest request) throws IOException {
        String jsonBody = objectMapper.writeValueAsString(request);
        RequestBody body = RequestBody.create(jsonBody, JSON);
        
        Request httpRequest = new Request.Builder()
                .url(SINGLE_SMS_URL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + pindoProperties.getToken())
                .build();

        try (Response response = client.newCall(httpRequest).execute()) {
            return response.body().string();
        }
    }

    public String sendBulkSms(BulkSmsRequest request) throws IOException {
        String jsonBody = objectMapper.writeValueAsString(request);
        RequestBody body = RequestBody.create(jsonBody, JSON);
        
        Request httpRequest = new Request.Builder()
                .url(BULK_SMS_URL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + pindoProperties.getToken())
                .build();

        try (Response response = client.newCall(httpRequest).execute()) {
            return response.body().string();
        }
    }
} 