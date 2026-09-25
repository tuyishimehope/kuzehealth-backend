package rw.ac.auca.kuzahealth.sms.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;
import rw.ac.auca.kuzahealth.sms.config.BackendProperties;
import rw.ac.auca.kuzahealth.sms.model.BulkSmsRecipient;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class BackendService {
    private final BackendProperties backendProperties;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public BackendService(BackendProperties backendProperties) {
        this.backendProperties = backendProperties;
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
        this.objectMapper = new ObjectMapper();
    }

    public List<BulkSmsRecipient> getPhoneNumbers() {
        try {
            String url = backendProperties.getBaseUrl() + backendProperties.getEndpoints().getGetPhoneNumbers();
            Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("Failed to fetch phone numbers. Response code: {}", response.code());
                    throw new RuntimeException("Failed to fetch phone numbers from backend");
                }

                String responseBody = response.body().string();
                return objectMapper.readValue(responseBody, new TypeReference<List<BulkSmsRecipient>>() {});
            }
        } catch (Exception e) {
            log.error("Error fetching phone numbers from backend: {}", e.getMessage(), e);
            throw new RuntimeException("Error fetching phone numbers from backend", e);
        }
    }
} 