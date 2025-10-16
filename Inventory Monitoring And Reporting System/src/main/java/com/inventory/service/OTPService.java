package com.inventory.service;

import java.util.*;
import jakarta.mail.*;
import jakarta.mail.internet.*;

public class OTPService {
    private static final Map<String, String> otpStorage = new HashMap<>();
    private static final Random random = new Random();


    public static String generateOTP(String email) {
        String otp = String.format("%06d", random.nextInt(999999));
        otpStorage.put(email, otp);
        return otp;
    }


    public static boolean verifyOTP(String email, String enteredOTP) {
        String storedOTP = otpStorage.get(email);
        return storedOTP != null && storedOTP.equals(enteredOTP);
    }


    public static void sendOTPEmail(String toEmail, String otp) {
        final String fromEmail = System.getenv("MAIL_USER");
        final String password = System.getenv("MAIL_PASS");

        if (fromEmail == null || password == null) {
            throw new RuntimeException("❌ Email credentials not set in environment variables!");
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("🔐 Your OTP Verification Code");

            message.setText("Hello,\n\nYour OTP for Inventory Management System registration is: "
                    + otp + "\n\nThis OTP is valid for 5 minutes.\n\n- Inventory System");

            Transport.send(message);
            System.out.println("✅ OTP sent successfully to " + toEmail);
        } catch (Exception e) {
            System.out.println("❌ Failed to send OTP: " + e.getMessage());
        }
    }
}
