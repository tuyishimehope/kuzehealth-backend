package rw.ac.auca.kuzahealth.core.user.service;

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import rw.ac.auca.kuzahealth.controller.auth.dto.EmailRequest;
import rw.ac.auca.kuzahealth.controller.auth.dto.LoginResponse;
import rw.ac.auca.kuzahealth.controller.auth.dto.OtpResponse;
import rw.ac.auca.kuzahealth.controller.auth.dto.ResetPasswordRequest;
import rw.ac.auca.kuzahealth.core.user.entity.User;
import rw.ac.auca.kuzahealth.core.user.enums.EUserType;
import rw.ac.auca.kuzahealth.core.user.repository.UserRepository;
import rw.ac.auca.kuzahealth.security.JwtService;
import rw.ac.auca.kuzahealth.sms.model.SmsRequest;
import rw.ac.auca.kuzahealth.sms.model.SmsResponse;
import rw.ac.auca.kuzahealth.sms.service.PindoSmsService;

import javax.swing.text.html.Option;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final PindoSmsService smsService;

    @Value("${mail.from}")
    private String fromEmail;

    @Value("${spring.mail.password}")
    private String password;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserService.class);

    public java.util.Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User registerUser(User user) {
        logger.info("Registering user with email: {}", user.getEmail());
        logger.info("User role from request in service: {}", user.getRole());

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User with this email already exists.");
        }

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        logger.info("Role being saved: {}", user.getRole());

        EUserType role = user.getRole() != null ? user.getRole() : EUserType.HEALTH_WORKER;
        logger.info("Role after null check: {}", role);

        User userData = User.create(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                hashedPassword,
                user.getPhoneNumber(),
                user.getUsername(),
                user.getOtpExpirationTime(),
                user.getProvince(),
                user.getDistrict(),
                user.getSector(),
                user.getDate_of_Birth(),
                user.getPosition(),
                user.getGender(),
                role
        );

        logger.info("User role after create: {}", userData.getRole());
        logger.info("Registering user: " + userData.getEmail());

        User savedUser = userRepository.save(userData);
        logger.info("User role after save: {}", savedUser.getRole());

        return savedUser;
    }

    public String verify(User user, String password) {
        logger.info("User Data: {} {}", user.getEmail(), user.getPassword());

        Optional<User> existingUserOpt = userRepository.findByEmail(user.getEmail());
        if (existingUserOpt.isEmpty()) {
            logger.error("User with email {} does not exist.", user.getEmail());
            return "User with email " + user.getEmail() + " does not exist.";
        }

        User existingUser = existingUserOpt.get();

        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), password));

        logger.info("Authentication: {}", authenticate);

        if (authenticate.isAuthenticated()) {
            logger.info("Authenticated user: {}", user.getEmail());
            return jwtService.generateToken(existingUser); // <-- FIXED
        }

        return "Authentication failed";
    }

    public OtpResponse sendOtp(EmailRequest userRequest) throws Exception {
        Optional<User> existingUserOpt;
        if (userRequest.getEmail().contains("@")) {
            existingUserOpt = userRepository.findByEmail(userRequest.getEmail());
        } else {
            existingUserOpt = userRepository.findByPhoneNumber(userRequest.getEmail());
        }

        if (existingUserOpt.isEmpty()) {
            return new OtpResponse("ERROR",
                    "User with provided email/phone number does not exist. Please register first.");
        }

        User existingUser = existingUserOpt.get();

        if (!passwordEncoder.matches(userRequest.getPassword(), existingUser.getPassword())) {
            return new OtpResponse("ERROR", "Invalid credentials. Please try again.");
        }

        String otp = generateOtp();
        existingUser.setOtp(otp);
        existingUser.setOtpExpirationTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(20));
        userRepository.save(existingUser);

        sendOtpViaEmail(existingUser.getEmail(), otp);

        SmsRequest smsRequest = new SmsRequest();
//        smsRequest.setTo("+250786125117");
        smsRequest.setTo(existingUser.getPhoneNumber());
        smsRequest.setText("Your OTP for account verification is: " + otp);
        smsRequest.setSender("PindoTest");
//        smsService.sendSingleSms(smsRequest);
        SmsResponse response = smsService.sendSingleSms(smsRequest.getTo(),smsRequest.getText(), smsRequest.getSender());

        System.out.println( response.isSuccess() ?
                ResponseEntity.ok(response) :
                ResponseEntity.badRequest().body(response));
        return new OtpResponse("SUCCESS", "OTP sent successfully. Please check your email.");
    }

    // Method to verify OTP and log the user in
    public LoginResponse verifyOtpAndLogin(User user, String otp) throws Exception {
        // Fetch user from the database
        User storedUser;
        if(user.getEmail().contains("@")) {
             storedUser = userRepository.findByEmail(user.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }else{
            storedUser = userRepository.findByPhoneNumber(user.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        }
        // Check if the OTP matches and is not expired
        if (storedUser.getOtp().equals(otp) && System.currentTimeMillis() < storedUser.getOtpExpirationTime()) {
            // OTP is valid, proceed with login (e.g., generate JWT token)
            String jwtToken = verify(storedUser, user.getPassword()); // Implement this method to generate JWT

            // Optionally, you could return the JWT token to the user for subsequent
            // requests
            System.out.println("Login successful. JWT Token: " + jwtToken);
            LoginResponse loginResponse = LoginResponse.builder()
                    .token(jwtToken)
                    .email(storedUser.getEmail())
                    .userType(storedUser.getRole().name())
                    .message("Login successful")
                    .build();
            return loginResponse;
        } else {
            throw new RuntimeException("Invalid or expired OTP");
        }
        // return "null";
    }

    // Helper method to generate a 6-digit OTP
    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generate a 6-digit OTP
        return String.valueOf(otp);
    }

    // Dummy method to send OTP via email (implement as needed)
    private String sendOtpViaEmail(String email, String otp) {
//        final String fromEmail = "tuyishimekyrie@gmail.com"; // Replace with your email
//        final String password = "zcyttvobemuzrpar"; // Use app password or token if needed

        // Setup mail server properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // TLS
        props.put("mail.smtp.host", "smtp.gmail.com"); // SMTP server
        props.put("mail.smtp.port", "587");

        // Create session
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            // Create email
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Verify Your Account - OTP Required");

            String htmlContent = "<!DOCTYPE html>" +
                    "<html lang='en'>" +
                    "<head>" +
                    "<meta charset='UTF-8'>" +
                    "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                    "<title>Your OTP Code</title>" +
                    "</head>" +
                    "<body style='margin:0;padding:0;font-family:-apple-system,BlinkMacSystemFont,\"Segoe UI\",Roboto,\"Helvetica Neue\",Arial,sans-serif;background:#f6f7fb;color:#0f172a;'>" +
                    "<table role='presentation' cellpadding='0' cellspacing='0' width='100%' style='background:#f6f7fb;padding:16px 0;'>" +
                    "<tr><td align='center'>" +
                    "<table role='presentation' cellpadding='0' cellspacing='0' width='100%' style='max-width:420px;background:#ffffff;border:1px solid #e5e7eb;border-radius:10px;overflow:hidden;'>" +
                    "<tr>" +
                    "<td style='padding:14px 16px;background:#111827;'>" +
                    "<h1 style='margin:0;font-size:16px;line-height:1.2;color:#ffffff;font-weight:600;'>Verify your email</h1>" +
                    "</td>" +
                    "</tr>" +
                    "<tr>" +
                    "<td style='padding:16px 16px 6px 16px;'>" +
                    "<p style='margin:0 0 8px 0;font-size:13px;color:#334155;'>Use the code below to continue. It expires in 20 minutes.</p>" +
                    "</td>" +
                    "</tr>" +
                    "<tr>" +
                    "<td style='padding:8px 16px 16px 16px;'>" +
                    "<table role='presentation' cellpadding='0' cellspacing='0' width='100%' style='background:#f3f4f6;border:1px solid #e5e7eb;border-radius:8px;'>" +
                    "<tr><td align='center' style='padding:14px;'>" +
                    "<div style='font-family:\"Courier New\",Courier,monospace;font-size:28px;letter-spacing:6px;font-weight:700;color:#0f172a;'>" + otp + "</div>" +
                    "</td></tr>" +
                    "</table>" +
                    "</td>" +
                    "</tr>" +
                    "<tr>" +
                    "<td style='padding:0 16px 12px 16px;'>" +
                    "<p style='margin:0;font-size:12px;color:#6b7280;'>If you didn’t request this code, you can safely ignore this email.</p>" +
                    "</td>" +
                    "</tr>" +
                    "<tr>" +
                    "<td style='border-top:1px solid #e5e7eb;padding:10px 16px;background:#fafafa;'>" +
                    "<p style='margin:0;font-size:11px;color:#94a3b8;text-align:center;'>© " + java.time.Year.now() + " KuzaHealth. Please do not reply to this email.</p>" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</td></tr>" +
                    "</table>" +
                    "</body>" +
                    "</html>";

            message.setContent(htmlContent, "text/html; charset=utf-8");

// Send email
            Transport.send(message);
            System.out.println("OTP email sent successfully to " + email);
            return "OTP sent successfully to " + email;

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Failed to send OTP email.");
            return "Failed to send OTP email: " + e.getMessage();
        }

    }

    public String getUserToken(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));
        return user.getOtp();
    }

    public boolean validateToken(String token, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));
        return token.equals(user.getOtp());
    }

    public void initiatePasswordReset(ResetPasswordRequest request) throws Exception {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new Exception("User not found"));

        // Generate a reset token
        String token = UUID.randomUUID().toString();
        long expirationTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(15); // 15 minutes validity

        // Store token and expiration time in the database
        user.setResetToken(token);
        user.setResetTokenExpiration(expirationTime);
        userRepository.save(user);

        // Send token to user's email
        sendResetEmail(request.getEmail(), token);
    }
    public void resetPassword(ResetPasswordRequest request) throws Exception {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new Exception("User not found"));

        logger.info("Resetting password for user: {}", user.getEmail());
        logger.info("Resetting password for user: {}", user.getResetToken());
        logger.info("Resetting password for user: {}", request.getToken().get());
        // Validate the reset token
        if (user.getResetToken() == null || !user.getResetToken().equals(request.getToken().get())) {
            throw new Exception("Invalid or expired reset token");
        }

        if (System.currentTimeMillis() > user.getResetTokenExpiration()) {
            throw new Exception("Reset token has expired");
        }

        // Update the user's password
        String hashedPassword = passwordEncoder.encode(request.getConfirmPassword().get());
        user.setPassword(hashedPassword);
        user.setResetToken(null); // Clear the reset token
        user.setResetTokenExpiration(0L); // Clear the expiration time
        userRepository.save(user);
    }

    private String sendResetEmail(String email, String token) {
//        final String fromEmail = "tuyishimekyrie@gmail.com"; // Replace with your email
//        final String password = "zcyttvobemuzrpar"; // Use app password or token if needed

        // Setup mail server properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // TLS
        props.put("mail.smtp.host", "smtp.gmail.com"); // SMTP server
        props.put("mail.smtp.port", "587");

        // Create session
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            // Create email
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Reset Your Password");

            String htmlContent = "<!DOCTYPE html>" +
                    "<html lang='en'>" +
                    "<head>" +
                    "<meta charset='UTF-8'>" +
                    "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                    "<title>Account Verification</title>" +
                    "</head>" +
                    "<body style='margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, \"Helvetica Neue\", Arial, sans-serif; background-color: #f8fafc; line-height: 1.6;'>" +

                    "<!-- Main Container -->" +
                    "<table cellpadding='0' cellspacing='0' border='0' width='100%' style='background-color: #f8fafc; padding: 40px 0;'>" +
                    "<tr>" +
                    "<td align='center'>" +

                    "<!-- Email Content -->" +
                    "<table cellpadding='0' cellspacing='0' border='0' width='100%' style='max-width: 600px; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05); overflow: hidden;'>" +

                    "<!-- Header -->" +
                    "<tr>" +
                    "<td style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 40px 30px; text-align: center;'>" +
                    "<h1 style='color: #ffffff; margin: 0; font-size: 28px; font-weight: 600; letter-spacing: -0.5px;'>Account Verification</h1>" +
                    "<p style='color: #e2e8f0; margin: 8px 0 0 0; font-size: 16px; opacity: 0.9;'>Secure access to your account</p>" +
                    "</td>" +
                    "</tr>" +

                    "<!-- Content -->" +
                    "<tr>" +
                    "<td style='padding: 50px 40px;'>" +
                    "<div style='text-align: center; margin-bottom: 40px;'>" +
                    "<div style='width: 80px; height: 80px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 50%; margin: 0 auto 20px; display: flex; align-items: center; justify-content: center; position: relative;'>" +
                    "<div style='width: 32px; height: 32px; border: 3px solid #ffffff; border-radius: 50%; border-top-color: transparent; animation: spin 1s linear infinite;'></div>" +
                    "</div>" +
                    "</div>" +

                    "<h2 style='color: #1e293b; margin: 0 0 16px 0; font-size: 24px; font-weight: 600; text-align: center;'>Verification Code</h2>" +
                    "<p style='color: #64748b; margin: 0 0 32px 0; font-size: 16px; text-align: center; line-height: 1.5;'>Enter the following verification code to complete your account setup. This code ensures the security of your account.</p>" +

                    "<!-- OTP Code Box -->" +
                    "<div style='background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%); border: 2px solid #e2e8f0; border-radius: 12px; padding: 30px; margin: 32px 0; text-align: center; position: relative; overflow: hidden;'>" +
                    "<div style='position: absolute; top: 0; left: 0; right: 0; height: 4px; background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);'></div>" +
                    "<div style='font-size: 36px; font-weight: 700; color: #1e293b; letter-spacing: 8px; font-family: \"Courier New\", Courier, monospace;'>" + token + "</div>" +
                    "<p style='color: #64748b; margin: 12px 0 0 0; font-size: 14px;'>Enter this code in the verification field</p>" +
                    "</div>" +

                    "<!-- Security Info -->" +
                    "<div style='background-color: #fef3c7; border-left: 4px solid #f59e0b; padding: 20px; border-radius: 8px; margin: 32px 0;'>" +
                    "<div style='display: flex; align-items: flex-start;'>" +
                    "<div style='color: #d97706; margin-right: 12px; font-size: 18px;'>⚠️</div>" +
                    "<div>" +
                    "<h4 style='color: #92400e; margin: 0 0 8px 0; font-size: 16px; font-weight: 600;'>Security Notice</h4>" +
                    "<ul style='color: #92400e; margin: 0; padding-left: 18px; font-size: 14px; line-height: 1.5;'>" +
                    "<li>This code expires in <strong>20 minutes</strong></li>" +
                    "<li>Never share this code with anyone</li>" +
                    "<li>If you didn't request this, please ignore this email</li>" +
                    "</ul>" +
                    "</div>" +
                    "</div>" +
                    "</div>" +

                    "<p style='color: #64748b; margin: 32px 0 0 0; font-size: 14px; text-align: center; line-height: 1.5;'>If you're having trouble with verification, please contact our support team for assistance.</p>" +
                    "</td>" +
                    "</tr>" +

                    "<!-- Footer -->" +
                    "<tr>" +
                    "<td style='background-color: #f8fafc; padding: 30px 40px; border-top: 1px solid #e2e8f0;'>" +
                    "<div style='text-align: center;'>" +
                    "<p style='color: #94a3b8; margin: 0 0 8px 0; font-size: 14px;'>Best regards,</p>" +
                    "<p style='color: #475569; margin: 0 0 16px 0; font-size: 16px; font-weight: 600;'>Your Health App Team</p>" +
                    "<div style='border-top: 1px solid #e2e8f0; padding-top: 20px; margin-top: 20px;'>" +
                    "<p style='color: #94a3b8; margin: 0; font-size: 12px; line-height: 1.4;'>This is an automated message. Please do not reply to this email.<br/>© 2025 Health App. All rights reserved.</p>" +
                    "</div>" +
                    "</div>" +
                    "</td>" +
                    "</tr>" +

                    "</table>" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +

                    "<!-- CSS Animation -->" +
                    "<style>" +
                    "@keyframes spin {" +
                    "0% { transform: rotate(0deg); }" +
                    "100% { transform: rotate(360deg); }" +
                    "}" +
                    "</style>" +

                    "</body>" +
                    "</html>";

            message.setContent(htmlContent, "text/html; charset=utf-8");

// Send email
            Transport.send(message);
            return "Request sent successfully " + email;

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Failed to send  email.");
            return "Failed to send  email: " + e.getMessage();
        }

    }


    public Object getAllUsers() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            logger.error("Error fetching users: {}", e.getMessage());
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    public User updateUser(UUID id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getFirstName() != null) {
            existingUser.setFirstName(user.getFirstName());
        }
        if (user.getLastName() != null) {
            existingUser.setLastName(user.getLastName());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(user.getPhoneNumber());
        }
        if (user.getUsername() != null) {
            existingUser.setUsername(user.getUsername());
        }
        if (user.getProvince() != null) {
            existingUser.setProvince(user.getProvince());
        }
        if (user.getDistrict() != null) {
            existingUser.setDistrict(user.getDistrict());
        }
        if (user.getSector() != null) {
            existingUser.setSector(user.getSector());
        }
        if (user.getDate_of_Birth() != null) {
            existingUser.setDate_of_Birth(user.getDate_of_Birth());
        }
        if (user.getPosition() != null) {
            existingUser.setPosition(user.getPosition());
        }
        if (user.getGender() != null) {
            existingUser.setGender(user.getGender());
        }
        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }
        if (user.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        logger.info("Updating user with ID: {}", id);
        User updatedUser = userRepository.save(existingUser);
        logger.info("User with ID: {} updated successfully", id);
        return updatedUser;
    }

    public void deleteUser(UUID id) {
    logger.info("Deleting user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
        logger.info("User with ID: {} deleted successfully", id);
    }

    public User getUserById(UUID id) {
    logger.info("Fetching user with ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
