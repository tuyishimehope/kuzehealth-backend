package rw.ac.auca.kuzahealth.config;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rw.ac.auca.kuzahealth.utils.ApiLoggingInterceptor;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/logging")
@Tag(name = "API Logging", description = "Interceptor that logs API requests and responses")
public class ApiLoggingController {

    private final ApiLoggingInterceptor interceptor;

    public ApiLoggingController(ApiLoggingInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent API logs", description = "Returns the last 100 API request/response logs in real-time")
    public List<String> getRecentLogs() {
        return interceptor.getRecentLogs();
    }
}
