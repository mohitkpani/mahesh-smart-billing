
package com.shopbill.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopbill.dto.CreateBillRequest;

@Service
public class EmailService {

        @Value("${sendliberty.api-key}")
        private String apiKey;

        private final ObjectMapper objectMapper = new ObjectMapper();
        private final HttpClient httpClient = HttpClient.newHttpClient();

        public void sendBillEmail(
                        CreateBillRequest request, byte[] pdfBytes) throws Exception {

                String customerName = request.customerName();

                if (customerName == null || customerName.isBlank()) {
                        customerName = "Customer";
                }

                String html = "<p>Dear " + escapeHtml(customerName.trim())
                                + ",</p>"
                                + "<p>Please find your invoice attached as a PDF.</p>"
                                + "<p>Thank you for your business!</p>"
                                + "<p>Regards,<br>Mahesh Digital Studio"
                                + "<br>Smart Billing System</p>";

                var attachment = java.util.Map.of(
                                "filename", "mahesh-digital-studio-invoice.pdf",
                                "content", Base64.getEncoder().encodeToString(pdfBytes));

                var payload = new java.util.HashMap<String, Object>();
                payload.put("from", "Mahesh Digital Studio <mytechlabs.tech@gmail.com>");
                payload.put("to", request.customerEmail());
                payload.put("subject", "Your Invoice from Mahesh Digital Studio");
                payload.put("html", html);
                payload.put("attachments", java.util.List.of(attachment));

                String json = objectMapper.writeValueAsString(payload);

                HttpRequest httpRequest = HttpRequest.newBuilder()
                                .uri(URI.create("https://sendliberty.com/api/send"))
                                .header("Authorization", "Bearer " + apiKey)
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(json))
                                .build();

                HttpResponse<String> response = httpClient.send(
                                httpRequest, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() < 200 || response.statusCode() >= 300) {
                        throw new IllegalStateException(
                                        "Sendliberty email request failed with HTTP "
                                                        + response.statusCode() + ": " + response.body());
                }
        }

        private String escapeHtml(String value) {
                return value.replace("&", "&amp;")
                                .replace("<", "&lt;")
                                .replace(">", "&gt;")
                                .replace("\"", "&quot;")
                                .replace("'", "&#39;");
        }
}
