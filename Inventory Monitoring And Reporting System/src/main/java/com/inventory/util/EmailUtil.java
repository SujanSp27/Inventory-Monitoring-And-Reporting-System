package com.inventory.util;

import java.io.File;

public class EmailUtil {
    public static void sendReport(String recipient, String subject, String message, String filePath) {
        File file = new File(filePath);

        // Check if the file exists
        if (!file.exists()) {
            System.out.println("❌ Report file not found: " + filePath);
            return;
        }

        // Simulated email output
        System.out.println("\n----------------------------------------------------");
        System.out.println("📧 Sending Email To: " + recipient);
        System.out.println("📌 Subject: " + subject);
        System.out.println("💬 Message: " + message);
        System.out.println("📎 Attachment: " + file.getName());
        System.out.println("✅ Email Sent Successfully (Simulation)");
        System.out.println("----------------------------------------------------");
    }
}