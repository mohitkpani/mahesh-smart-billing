package com.shopbill.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.shopbill.dto.BillRequest;
import com.shopbill.dto.CreateBillRequest;

@Service
public class PdfService {

    private static final Color PURPLE = new Color(109, 40, 217);
    private static final Color DARK_PURPLE = new Color(76, 29, 149);
    private static final Color LIGHT_PURPLE = new Color(237, 233, 254);
    private static final Color BORDER = new Color(221, 214, 254);
    private static final Color DARK = new Color(40, 40, 40);
    private static final Color MUTED = new Color(90, 90, 100);

    private static final String SHOP_NAME = "Mahesh Digital Studio";
    private static final String SHOP_PHONE = "83277 68671";
    private static final String SHOP_ADDRESS =
            "Totapada, Rasgovindpur, Mayurbhanj, Odisha, 757056";

    public byte[] generateBillPdf(CreateBillRequest request) throws Exception {
        List<BillRequest> items = request.items() == null ? List.of() : request.items();
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        
        Document document = new Document(PageSize.A4, 28, 28, 24, 78);
        PdfWriter writer = PdfWriter.getInstance(document, output);
        writer.setPageEvent(new InvoiceFooterPageEvent());
        document.open();

        Font shopFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.WHITE);
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, DARK_PURPLE);
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, DARK_PURPLE);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 8.5f, DARK);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.WHITE);
        Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, DARK_PURPLE);

       
        PdfPTable shopHeader = new PdfPTable(1);
        shopHeader.setWidthPercentage(100);
        shopHeader.setSpacingAfter(6);

        PdfPCell headerCell = new PdfPCell();
        headerCell.setBorder(Rectangle.NO_BORDER);
        headerCell.setBackgroundColor(PURPLE);
        headerCell.setPadding(0);

        PdfPTable headerContent = new PdfPTable(new float[] { 1, 5, 1 });
        headerContent.setWidthPercentage(100);
        headerContent.setWidths(new float[] { 1, 5, 1 });

        // Left column: logo.
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setBackgroundColor(PURPLE);
        logoCell.setPadding(4);
        logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        try {
            ClassPathResource logoResource =
                    new ClassPathResource("static/images/mds-logo.png");
            if (logoResource.exists()) {
                try (InputStream input = logoResource.getInputStream()) {
                    Image logo = Image.getInstance(input.readAllBytes());
                    logo.scaleToFit(42, 42);
                    logoCell.addElement(logo);
                }
            } else {
                System.err.println("PDF logo not found: static/images/mds-logo.png");
            }
        } catch (Exception logoException) {
            System.err.println("Could not load PDF logo: " + logoException.getMessage());
        }
        headerContent.addCell(logoCell);

        // Middle column: shop name, phone and address.
        PdfPCell infoCell = new PdfPCell();
        infoCell.setBorder(Rectangle.NO_BORDER);
        infoCell.setBackgroundColor(PURPLE);
        infoCell.setPaddingTop(7);
        infoCell.setPaddingBottom(7);
        infoCell.setPaddingLeft(0);
        infoCell.setPaddingRight(0);
        infoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Paragraph shopName = new Paragraph(SHOP_NAME, shopFont);
        shopName.setAlignment(Element.ALIGN_CENTER);
        shopName.setSpacingAfter(2);
        infoCell.addElement(shopName);

        Paragraph phone = new Paragraph(
                "Phone: " + SHOP_PHONE,
                FontFactory.getFont(FontFactory.HELVETICA, 8, Color.WHITE));
        phone.setAlignment(Element.ALIGN_CENTER);
        phone.setSpacingAfter(2);
        infoCell.addElement(phone);

        Paragraph address = new Paragraph(
                SHOP_ADDRESS,
                FontFactory.getFont(FontFactory.HELVETICA, 8, Color.WHITE));
        address.setAlignment(Element.ALIGN_CENTER);
        infoCell.addElement(address);
        headerContent.addCell(infoCell);

        // Right spacer balances the logo column so the shop details remain centered.
        PdfPCell spacerCell = new PdfPCell();
        spacerCell.setBorder(Rectangle.NO_BORDER);
        spacerCell.setBackgroundColor(PURPLE);
        spacerCell.setPadding(4);
        headerContent.addCell(spacerCell);

        headerCell.addElement(headerContent);
        shopHeader.addCell(headerCell);
        document.add(shopHeader);

        Paragraph billTitle = new Paragraph("BILL RECEIPT", titleFont);
        billTitle.setAlignment(Element.ALIGN_CENTER);
        billTitle.setSpacingBefore(1);
        billTitle.setSpacingAfter(4);
        document.add(billTitle);

        Paragraph date = new Paragraph(
                "Date: " + LocalDate.now(),
                FontFactory.getFont(FontFactory.HELVETICA, 8, MUTED));
        date.setAlignment(Element.ALIGN_RIGHT);
        date.setSpacingAfter(7);
        document.add(date);

        // Customer details section.
        Paragraph customerHeading = new Paragraph("CUSTOMER DETAILS", sectionFont);
        customerHeading.setSpacingAfter(4);
        document.add(customerHeading);

        PdfPTable customerTable = new PdfPTable(new float[] { 1.15f, 2.2f, 1.15f, 2.2f });
        customerTable.setWidthPercentage(100);
        customerTable.setSpacingAfter(8);
        customerTable.setKeepTogether(true);

        addCustomerCell(customerTable, "Customer Name", normalFont, true);
        addCustomerCell(customerTable, getDisplayValue(request.customerName()), normalFont, false);
        addCustomerCell(customerTable, "Mobile Number", normalFont, true);
        addCustomerCell(customerTable, getDisplayValue(request.mobileNumber()), normalFont, false);
        addCustomerCell(customerTable, "Email Address", normalFont, true);
        addCustomerCell(customerTable, getDisplayValue(request.customerEmail()), normalFont, false);

        // Reads customerAddress() if that component exists in CreateBillRequest.
        String customerAddress = getOptionalAddress(request);
        addCustomerCell(customerTable, "Address", normalFont, true);
        addCustomerCell(customerTable, customerAddress, normalFont, false);

        document.add(customerTable);

        Paragraph itemsHeading = new Paragraph("PRODUCT / SERVICE DETAILS", sectionFont);
        itemsHeading.setSpacingAfter(4);
        document.add(itemsHeading);

        PdfPTable table = new PdfPTable(new float[] { 4.2f, 1.6f, 1.2f, 1.8f });
        table.setWidthPercentage(100);
        table.setSpacingAfter(8);
        table.setHeaderRows(1);
        table.setSplitRows(true);
        table.setSplitLate(false);

        addHeaderCell(table, "Product / Service", headerFont);
        addHeaderCell(table, "Price (Rs.)", headerFont);
        addHeaderCell(table, "Qty", headerFont);
        addHeaderCell(table, "Amount (Rs.)", headerFont);

        double total = 0.0;
        for (BillRequest item : items) {
            if (item == null) {
                continue;
            }

            double price = item.price() == null ? 0.0 : item.price();
            int quantity = item.quantity() == null ? 0 : item.quantity();
            double amount = price * quantity;
            total += amount;

            addBodyCell(table, getDisplayValue(item.productName()), normalFont, Element.ALIGN_LEFT);
            addBodyCell(table, String.format("%.2f", price), normalFont, Element.ALIGN_RIGHT);
            addBodyCell(table, String.valueOf(quantity), normalFont, Element.ALIGN_CENTER);
            addBodyCell(table, String.format("%.2f", amount), normalFont, Element.ALIGN_RIGHT);
        }
        document.add(table);

        // Total follows the complete item table, including multi-page bills.
        PdfPTable totalTable = new PdfPTable(1);
        totalTable.setWidthPercentage(100);

        PdfPCell totalCell = new PdfPCell(new Phrase(
                String.format("GRAND TOTAL: Rs. %.2f", total), totalFont));
        totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalCell.setBackgroundColor(LIGHT_PURPLE);
        totalCell.setPadding(7);
        totalCell.setBorderColor(PURPLE);
        totalTable.addCell(totalCell);
        totalTable.setKeepTogether(true);
        document.add(totalTable);

        document.close();
        return output.toByteArray();
    }

    private String getOptionalAddress(CreateBillRequest request) {
        try {
            Object value = request.getClass().getMethod("customerAddress").invoke(request);
            return getDisplayValue(value == null ? null : value.toString());
        } catch (Exception ignored) {
            return "Not provided";
        }
    }

    private String getDisplayValue(String value) {
        if (value == null || value.isBlank()) {
            return "Not provided";
        }
        return value.trim();
    }

    private void addCustomerCell(PdfPTable table, String text, Font font, boolean label) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(4);
        cell.setBorderColor(BORDER);
        cell.setBackgroundColor(label ? LIGHT_PURPLE : Color.WHITE);
        table.addCell(cell);
    }

    private void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(PURPLE);
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorderColor(Color.WHITE);
        table.addCell(cell);
    }

    private void addBodyCell(PdfPTable table, String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(4);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorderColor(BORDER);
        cell.setBackgroundColor(Color.WHITE);
        table.addCell(cell);
    }

    /**
     * Draws the footer at a fixed position near the bottom of every PDF page.
     * This does not consume document-flow space, so invoice content can continue
     * naturally above it.
     */
    private static class InvoiceFooterPageEvent extends com.lowagie.text.pdf.PdfPageEventHelper {

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            com.lowagie.text.pdf.PdfContentByte canvas = writer.getDirectContent();
            float centerX = (document.left() + document.right()) / 2f;
            float footerY = document.bottom() - 12f;

            Font footerFont = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD, 11, DARK_PURPLE);
            Font visitFont = FontFactory.getFont(
                    FontFactory.HELVETICA, 9, MUTED);
            Font brandFont = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD, 8, PURPLE);

            try {
                com.lowagie.text.pdf.ColumnText.showTextAligned(
                        canvas, Element.ALIGN_CENTER,
                        new Phrase("Thank you for your business!", footerFont),
                        centerX, footerY + 22f, 0);

                com.lowagie.text.pdf.ColumnText.showTextAligned(
                        canvas, Element.ALIGN_CENTER,
                        new Phrase("We look forward to serving you again!", visitFont),
                        centerX, footerY + 9f, 0);

                com.lowagie.text.pdf.ColumnText.showTextAligned(
                        canvas, Element.ALIGN_CENTER,
                        new Phrase(SHOP_NAME, brandFont),
                        centerX, footerY - 4f, 0);
            } catch (Exception exception) {
                throw new RuntimeException("Unable to draw invoice footer", exception);
            }
        }
    }

}
