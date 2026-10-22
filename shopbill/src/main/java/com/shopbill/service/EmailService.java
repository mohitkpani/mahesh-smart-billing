
package com.shopbill.service;

import java.io.ByteArrayInputStream;

import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.shopbill.dto.CreateBillRequest;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendBillEmail(
            CreateBillRequest request,
            byte[] pdfBytes) throws Exception {

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(
                message, true, "UTF-8");

        helper.setTo(request.customerEmail());

        helper.setSubject(
                "Your Invoice from Mahesh Digital Studio");

        String customerName = request.customerName();

        if (customerName == null || customerName.isBlank()) {
            customerName = "Customer";
        }

        helper.setText(
                "Dear " + customerName.trim() + ",\n\n"
                + "Please find your invoice attached as a PDF.\n\n"
                + "Thank you for your business!\n\n"
                + "Regards,\n"
                + "Mahesh Digital Studio\n"
                + "Smart Billing System");

        InputStreamSource attachment = () ->
                new ByteArrayInputStream(pdfBytes);

        helper.addAttachment(
                "mahesh-digital-studio-invoice.pdf",
                attachment);

        // Send the email
        mailSender.send(message);
    }
}
