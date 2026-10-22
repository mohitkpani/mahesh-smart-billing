
package com.shopbill.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopbill.dto.CreateBillRequest;
import com.shopbill.service.PdfService;

@RestController
@RequestMapping("/api/billing")
public class PdfController {

    private final PdfService pdfService;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @PostMapping(
            value = "/pdf",
            produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generatePdf(
            @RequestBody CreateBillRequest request) throws Exception {

        byte[] pdf = pdfService.generateBillPdf(request);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=dilu-babu-grocery-receipt.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
