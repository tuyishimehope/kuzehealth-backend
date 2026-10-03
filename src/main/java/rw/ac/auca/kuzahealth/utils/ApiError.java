package rw.ac.auca.kuzahealth.utils;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Body returned for every failed request.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(String error, String message, LocalDateTime timestamp, int status,
        Map<String, String> fieldErrors) {

    public static ApiError of(org.springframework.http.HttpStatusCode status, String error, String message) {
        return new ApiError(error, message, LocalDateTime.now(), status.value(), null);
    }
}
