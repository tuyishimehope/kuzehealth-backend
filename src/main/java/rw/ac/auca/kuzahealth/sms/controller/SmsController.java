package rw.ac.auca.kuzahealth.sms.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import rw.ac.auca.kuzahealth.core.exception.BadRequestException;
import rw.ac.auca.kuzahealth.core.notification.NotificationService;
import rw.ac.auca.kuzahealth.core.notification.SmsLog;
import rw.ac.auca.kuzahealth.core.notification.SmsPurpose;
import rw.ac.auca.kuzahealth.core.notification.SmsStatus;
import rw.ac.auca.kuzahealth.sms.model.BulkSmsRecipient;
import rw.ac.auca.kuzahealth.sms.model.BulkSmsRequest;
import rw.ac.auca.kuzahealth.sms.model.SingleSmsRequest;
import rw.ac.auca.kuzahealth.sms.model.SmsResponse;
import rw.ac.auca.kuzahealth.sms.service.BackendService;
import rw.ac.auca.kuzahealth.utils.paging.PageRequests;
import rw.ac.auca.kuzahealth.utils.paging.PageResponse;

@RestController
@RequestMapping({ "/api/sms", "/api/v1/sms" })
@Slf4j
@RequiredArgsConstructor
public class SmsController {

    private static final int MAX_BULK_RECIPIENTS = 500;

    private final NotificationService notificationService;
    private final BackendService backendService;

    @PostMapping("/send")
    public ResponseEntity<SmsResponse> sendSms(@RequestBody SingleSmsRequest request) {
        if (isBlank(request.getTo()) || isBlank(request.getText())) {
            throw new BadRequestException("to and text are required");
        }
        SmsLog log = notificationService.sendDirect(request.getTo(), request.getText(), request.getSender(),
                SmsPurpose.MANUAL, request.getText());
        SmsResponse response = new SmsResponse(log.getStatus().isSent(),
                log.getStatus().isSent() ? "SMS sent successfully" : "Failed to send SMS");

        return response.isSuccess() ?
            ResponseEntity.ok(response) :
            ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/send-bulk")
    public ResponseEntity<SmsResponse> sendBulkSms(@RequestBody BulkSmsRequest request) {
        return sendBulk(request.getRecipients(), request);
    }

    @PostMapping("/send-bulk-from-backend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SmsResponse> sendBulkSmsFromBackend(@RequestBody BulkSmsRequest request) {
        try {
            return sendBulk(backendService.getPhoneNumbers(), request);
        } catch (RuntimeException e) {
            log.error("Error sending bulk SMS from backend: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new SmsResponse(false, "Error sending bulk SMS"));
        }
    }

    /** Every SMS the system sent or skipped, newest first. */
    @GetMapping("/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<SmsLog> logs(@RequestParam(required = false) SmsStatus status,
            @RequestParam(required = false) SmsPurpose purpose,
            @RequestParam(required = false) UUID parentId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size) {
        return PageResponse.of(notificationService.search(status, purpose, parentId,
                PageRequests.of(page, size, null)));
    }

    private ResponseEntity<SmsResponse> sendBulk(List<BulkSmsRecipient> recipients, BulkSmsRequest request) {
        if (recipients == null || recipients.isEmpty() || isBlank(request.getText())) {
            throw new BadRequestException("recipients and text are required");
        }
        if (recipients.size() > MAX_BULK_RECIPIENTS) {
            throw new BadRequestException("A bulk message can have at most " + MAX_BULK_RECIPIENTS + " recipients");
        }
        SmsResponse response = notificationService.sendBulk(recipients, request.getText(), request.getSender(),
                SmsPurpose.MANUAL);
        return response.isSuccess() ?
            ResponseEntity.ok(response) :
            ResponseEntity.badRequest().body(response);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
