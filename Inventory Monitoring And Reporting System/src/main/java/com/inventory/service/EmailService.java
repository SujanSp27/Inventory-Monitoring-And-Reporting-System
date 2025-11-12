package com.inventory.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.File;
import java.util.Properties;

public class EmailService {

    // ✅ Method 1: Send report with optional file attachment (used in StockAlertService)
    public static void sendReport(String toEmail, String subject, String body, String attachmentPath) {
        final String fromEmail = System.getenv("MAIL_USER");
        final String password = System.getenv("MAIL_PASS");

        // ✅ Step 1: Validate credentials with better error handling
        if (fromEmail == null || fromEmail.trim().isEmpty()) {
            System.out.println("❌ MAIL_USER environment variable is not set!");
            System.out.println("💡 Please set MAIL_USER environment variable with your email address");
            return;
        }
        
        if (password == null || password.trim().isEmpty()) {
            System.out.println("❌ MAIL_PASS environment variable is not set!");
            System.out.println("💡 Please set MAIL_PASS environment variable with your email password or app password");
            return;
        }

        // ✅ Step 2: Validate recipient email
        if (toEmail == null || toEmail.trim().isEmpty()) {
            System.out.println("❌ Recipient email is null or empty!");
            return;
        }
        toEmail = toEmail.trim();

        if (!toEmail.contains("@") || toEmail.endsWith("@")) {
            System.out.println("❌ Invalid recipient email format: " + toEmail);
            return;
        }

        // ✅ Step 3: SMTP configuration
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
            // ✅ Step 4: Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            message.setSubject(subject);

            // ✅ Step 5: Message body
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(body);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);

            // ✅ Step 6: Optional file attachment (handle null safely)
            if (attachmentPath != null && !attachmentPath.trim().isEmpty()) {
                File file = new File(attachmentPath);
                if (file.exists() && file.isFile()) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    attachmentPart.attachFile(file);
                    multipart.addBodyPart(attachmentPart);
                } else {
                    System.out.println("⚠️ Attachment not found or invalid: " + attachmentPath);
                }
            }

            message.setContent(multipart);

            // ✅ Step 7: Send email
            Transport.send(message);
            System.out.println("\n📨 Report email sent successfully to " + toEmail + "\n");


        } catch (Exception e) {
            System.out.println("❌ Error sending report email: " + e.getMessage());
        }
    }

    // ✅ Method 2: Send simple text email (without attachments)
    public void sendEmail(String toEmail, String subject, String body) {
        final String fromEmail = System.getenv("MAIL_USER");
        final String password = System.getenv("MAIL_PASS");

        // ✅ Step 1: Validate credentials with better error handling
        if (fromEmail == null || fromEmail.trim().isEmpty()) {
            System.out.println("❌ MAIL_USER environment variable is not set!");
            System.out.println("💡 Please set MAIL_USER environment variable with your email address");
            return;
        }
        
        if (password == null || password.trim().isEmpty()) {
            System.out.println("❌ MAIL_PASS environment variable is not set!");
            System.out.println("💡 Please set MAIL_PASS environment variable with your email password or app password");
            return;
        }

        if (toEmail == null || toEmail.trim().isEmpty()) {
            System.out.println("❌ Recipient email is null or empty!");
            return;
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
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("📧 Email sent successfully to " + toEmail);

        } catch (MessagingException e) {
            System.out.println("❌ Failed to send email: " + e.getMessage());
        }
    }
}
