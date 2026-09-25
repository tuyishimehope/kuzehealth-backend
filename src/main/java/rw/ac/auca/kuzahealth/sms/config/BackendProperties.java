package rw.ac.auca.kuzahealth.sms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "backend")
public class BackendProperties {
    private String baseUrl;
    private Endpoints endpoints = new Endpoints();

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Endpoints getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Endpoints endpoints) {
        this.endpoints = endpoints;
    }

    public static class Endpoints {
        private String getPhoneNumbers;

        public String getGetPhoneNumbers() {
            return getPhoneNumbers;
        }

        public void setGetPhoneNumbers(String getPhoneNumbers) {
            this.getPhoneNumbers = getPhoneNumbers;
        }
    }
} 