package rw.ac.auca.kuzahealth.controller.nutrition;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rw.ac.auca.kuzahealth.controller.nutrition.dto.NutritionRequest;
import rw.ac.auca.kuzahealth.core.exception.BadRequestException;
import rw.ac.auca.kuzahealth.core.notification.NotificationService;
import rw.ac.auca.kuzahealth.core.notification.SmsLog;
import rw.ac.auca.kuzahealth.core.notification.SmsPurpose;
import rw.ac.auca.kuzahealth.sms.model.SmsResponse;

@RestController
@RequestMapping({ "/api/nutrition-info", "/api/v1/nutrition-info" })
@RequiredArgsConstructor
public class Nutrition {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<?> sendNutritionData(@RequestBody NutritionRequest request) {
        if (request.phoneNumber == null || request.phoneNumber.isBlank()
                || request.message == null || request.message.isBlank()) {
            throw new BadRequestException("phoneNumber and message are required");
        }
        SmsLog log = notificationService.sendDirect(request.phoneNumber, request.message, null,
                SmsPurpose.NUTRITION, request.message);

        SmsResponse response = new SmsResponse(log.getStatus().isSent(),
                log.getStatus().isSent() ? "SMS sent successfully" : "Failed to send SMS");
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }
}
