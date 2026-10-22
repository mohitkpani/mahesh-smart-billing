
package com.shopbill.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.shopbill.dto.CreateBillRequest;
import com.shopbill.service.EmailService;
import com.shopbill.service.PdfService;

@RestController
@RequestMapping("/api/billing")
public class EmailController {

    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);

    private final EmailService emailService;
    private final PdfService pdfService;

    public EmailController(
            EmailService emailService,
            PdfService pdfService) {
        this.emailService = emailService;
        this.pdfService = pdfService;
    }

    @PostMapping("/email")
    public ResponseEntity<Map<String, String>> emailBill(
            @RequestBody CreateBillRequest request) {

        try {
            if (request.items() == null || request.items().isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "At least one bill item is required");
            }

            byte[] pdf = pdfService.generateBillPdf(request);

            String email = request.customerEmail();

            if (email == null || email.isBlank()) {
                return ResponseEntity.ok(
                        Map.of(
                                "message",
                                "Bill generated successfully. No email provided."));
            }

            if (!email.matches(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Enter a valid customer email address");
            }

            emailService.sendBillEmail(request, pdf);

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Bill generated and emailed successfully"));

        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Failed to generate bill or send invoice email", ex);

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not process the bill or send the email",
                    ex);
        }
    }
}
