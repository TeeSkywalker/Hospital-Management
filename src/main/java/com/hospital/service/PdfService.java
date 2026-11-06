package com.hospital.service;

import com.hospital.model.Bill;
import com.hospital.model.BillItem;
import com.lowagie.text.Document;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class PdfService {
    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    public byte[] receipt(Bill b) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document d = new Document();
        PdfWriter.getInstance(d, out);
        d.open();
        d.add(new Paragraph("Hospital Management System", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
        d.add(new Paragraph((b.getPaidAt() != null ? "Receipt" : "Invoice") + " - Bill #" + b.getId()));
        d.add(new Paragraph("Date: " + b.getCreatedAt().format(F)));
        d.add(new Paragraph("Patient: " + b.getPatient().getFullName()));
        d.add(new Paragraph(" "));
        PdfPTable t = new PdfPTable(new float[] {4, 1});
        t.setWidthPercentage(100);
        t.addCell("Item");
        t.addCell("Amount");
        for (BillItem i : b.getItems()) {
            t.addCell(i.getDescription());
            t.addCell(i.getAmount().toPlainString());
        }
        t.addCell("TOTAL");
        t.addCell(b.getTotal().toPlainString());
        d.add(t);
        d.add(new Paragraph(" "));
        d.add(new Paragraph("Status: " + b.getStatus()
            + (b.getPaymentMethod() != null ? " (" + b.getPaymentMethod() + ")" : "")));
        d.close();
        return out.toByteArray();
    }

    public byte[] summary(Map<String, String> lines) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document d = new Document();
        PdfWriter.getInstance(d, out);
        d.open();
        d.add(new Paragraph("Hospital Summary Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
        d.add(new Paragraph(" "));
        PdfPTable t = new PdfPTable(new float[] {3, 2});
        t.setWidthPercentage(100);
        for (Map.Entry<String, String> e : lines.entrySet()) {
            t.addCell(e.getKey());
            t.addCell(e.getValue());
        }
        d.add(t);
        d.close();
        return out.toByteArray();
    }
}
