package rw.ac.auca.kuzahealth.core.user.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.Year;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Sends the account emails (login code, password reset) through the configured
 * Spring Mail transport.
 */
@Service
public class AuthMailService {

    private static final Logger logger = LoggerFactory.getLogger(AuthMailService.class);

    private final JavaMailSender mailSender;
    private final String fromEmail;
    private final String otpTemplate;
    private final String resetTemplate;

    public AuthMailService(JavaMailSender mailSender, @Value("${mail.from}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
        this.otpTemplate = load("mail/otp.html");
        this.resetTemplate = load("mail/reset.html");
    }

    public boolean sendOtp(String email, String otp, long validMinutes) {
        return send(email, "Verify Your Account - OTP Required", render(otpTemplate, otp, validMinutes));
    }

    public boolean sendPasswordReset(String email, String token, long validMinutes) {
        return send(email, "Reset Your Password", render(resetTemplate, token, validMinutes));
    }

    private boolean send(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            if (fromEmail != null && !fromEmail.isBlank()) {
                helper.setFrom(fromEmail);
            }
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            return true;
        } catch (MessagingException | MailException e) {
            logger.error("Failed to send '{}' email: {}", subject, e.getMessage());
            return false;
        }
    }

    private static String render(String template, String code, long validMinutes) {
        return template
                .replace("{{code}}", code)
                .replace("{{minutes}}", String.valueOf(validMinutes))
                .replace("{{year}}", String.valueOf(Year.now()));
    }

    private static String load(String path) {
        try {
            return new String(new ClassPathResource(path).getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Missing mail template " + path, e);
        }
    }
}
