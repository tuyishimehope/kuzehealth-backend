package rw.ac.auca.kuzahealth.utils;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiMetricsInterceptor implements HandlerInterceptor {

    private final MeterRegistry meterRegistry;

    public ApiMetricsInterceptor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (handler instanceof HandlerMethod method) {
            String endpoint = method.getMethod().getDeclaringClass().getSimpleName() + "." + method.getMethod().getName();

            meterRegistry.counter("api.requests.total",
                    "endpoint", endpoint,
                    "method", request.getMethod(),
                    "status", String.valueOf(response.getStatus())
            ).increment();
        }
    }
}
