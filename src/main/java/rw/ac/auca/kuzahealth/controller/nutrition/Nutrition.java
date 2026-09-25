package rw.ac.auca.kuzahealth.controller.nutrition;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rw.ac.auca.kuzahealth.controller.nutrition.dto.NutritionRequest;
import rw.ac.auca.kuzahealth.sms.model.SmsRequest;
import rw.ac.auca.kuzahealth.sms.model.SmsResponse;
import rw.ac.auca.kuzahealth.sms.service.PindoSmsService;

@RestController
@RequestMapping("/api/nutrition-info")
@RequiredArgsConstructor
public class Nutrition {

    private final PindoSmsService smsService;

    @PostMapping
    public ResponseEntity<?> sendNutritionData(@RequestBody NutritionRequest request) {
        SmsRequest smsRequest = new SmsRequest();
        smsRequest.setTo(request.phoneNumber);
        smsRequest.setText(request.message);
        smsRequest.setSender("PindoTest");

        SmsResponse response = smsService.sendSingleSms(
                smsRequest.getTo(),
                smsRequest.getText(),
                smsRequest.getSender()
        );

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }
}

