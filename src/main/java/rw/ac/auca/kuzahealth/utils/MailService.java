package rw.ac.auca.kuzahealth.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;
    @Value("${mail.from}")
    private String fromEmail;
    public String sendOtp(String toEmail) {
        // Generate a random 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));

        // Create the email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP is: " + otp);

        // Send the email
        mailSender.send(message);

        // Return the generated OTP for verification
        return otp;
    }

    /**
     * Send a general email
     * 
     * @param toEmail the recipient's email address
     * @param subject the email subject
     * @param text the email body
     */
    public void sendEmail(String toEmail, String subject, String text) {
        // Create the email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(text);

        // Send the email
        mailSender.send(message);
    }
}
