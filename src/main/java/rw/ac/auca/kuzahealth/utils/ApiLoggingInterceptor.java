package rw.ac.auca.kuzahealth.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
public class ApiLoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ApiLoggingInterceptor.class);

    // Store last 100 logs for frontend
    private final Deque<String> recentLogs = new ConcurrentLinkedDeque<>();
    private static final int MAX_LOGS = 100;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);

        String entry = "Incoming API request: " + request.getMethod() + " " + request.getRequestURI();
        log.info(entry);
        addLog(entry);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Object startAttr = request.getAttribute("startTime");
        Long startTime = (startAttr instanceof Long) ? (Long) startAttr : null;

        String exit;
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            exit = String.format("Completed API request: %s %s | Status: %d | Time: %d ms",
                    request.getMethod(), request.getRequestURI(), response.getStatus(), duration);
        } else {
            exit = String.format("Completed API request: %s %s | Status: %d",
                    request.getMethod(), request.getRequestURI(), response.getStatus());
        }

        log.info(exit);
        addLog(exit);
    }

    private void addLog(String logEntry) {
        if (recentLogs.size() >= MAX_LOGS) {
            recentLogs.pollFirst();
        }
        recentLogs.addLast(logEntry);
    }

    // Expose recent logs for frontend polling
    public List<String> getRecentLogs() {
        return new ArrayList<>(recentLogs);
    }
}
