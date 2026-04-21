package com.financeos.service;

import com.financeos.entity.Transaction;
import com.financeos.entity.User;
import com.financeos.repository.TransactionRepository;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfExportService {

    private final TransactionRepository transactionRepository;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DeviceRgb HEADER_COLOR = new DeviceRgb(108, 99, 255);
    private static final DeviceRgb GREEN = new DeviceRgb(0, 229, 160);
    private static final DeviceRgb RED   = new DeviceRgb(255, 77, 109);

    public byte[] exportTransactions(User user, LocalDate startDate, LocalDate endDate) {
        try {
            var baos = new ByteArrayOutputStream();
            var writer = new PdfWriter(baos);
            var pdf = new PdfDocument(writer);
            var doc = new Document(pdf);
            var boldFont  = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            var normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Title
            doc.add(new Paragraph("FinanceOS — Relatório de Transações")
                .setFont(boldFont).setFontSize(20)
                .setFontColor(HEADER_COLOR).setTextAlignment(TextAlignment.CENTER));

            doc.add(new Paragraph("Período: " + startDate.format(DATE_FMT) + " a " + endDate.format(DATE_FMT))
                .setFont(normalFont).setFontSize(11)
                .setTextAlignment(TextAlignment.CENTER).setMarginBottom(20));

            // Fetch transactions
            var transactions = transactionRepository.findByUserAndDateRange(user.getId(), startDate, endDate);

            // Summary
            var totalIncome   = sum(transactions, Transaction.TransactionType.INCOME);
            var totalExpenses = sum(transactions, Transaction.TransactionType.EXPENSE);
            var balance       = totalIncome.subtract(totalExpenses);

            var summaryTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1})).useAllAvailableWidth();
            addSummaryCell(summaryTable, "Receitas", "R$ " + totalIncome, GREEN, boldFont);
            addSummaryCell(summaryTable, "Despesas", "R$ " + totalExpenses, RED, boldFont);
            addSummaryCell(summaryTable, "Saldo", "R$ " + balance,
                balance.compareTo(BigDecimal.ZERO) >= 0 ? GREEN : RED, boldFont);
            doc.add(summaryTable.setMarginBottom(20));

            // Table header
            var table = new Table(UnitValue.createPercentArray(new float[]{2, 3, 1.5f, 1.5f, 2})).useAllAvailableWidth();
            for (String h : List.of("Data", "Descrição", "Categoria", "Tipo", "Valor")) {
                table.addHeaderCell(new Cell().add(new Paragraph(h).setFont(boldFont).setFontSize(10))
                    .setBackgroundColor(HEADER_COLOR).setFontColor(ColorConstants.WHITE));
            }

            // Rows
            for (var t : transactions) {
                boolean isIncome = t.getType() == Transaction.TransactionType.INCOME;
                table.addCell(cell(t.getDate().format(DATE_FMT), normalFont));
                table.addCell(cell(t.getDescription(), normalFont));
                table.addCell(cell(t.getCategory() != null ? t.getCategory().getName() : "-", normalFont));
                table.addCell(new Cell().add(new Paragraph(isIncome ? "Receita" : "Despesa")
                    .setFont(boldFont).setFontSize(9)
                    .setFontColor(isIncome ? GREEN : RED)));
                table.addCell(new Cell().add(new Paragraph("R$ " + t.getAmount())
                    .setFont(boldFont).setFontSize(9)
                    .setFontColor(isIncome ? GREEN : RED)));
            }
            doc.add(table);

            doc.add(new Paragraph("Gerado em " + LocalDate.now().format(DATE_FMT) + " via FinanceOS")
                .setFont(normalFont).setFontSize(8)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.RIGHT).setMarginTop(20));

            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }

    private void addSummaryCell(Table t, String label, String value, DeviceRgb color, com.itextpdf.kernel.font.PdfFont font) {
        t.addCell(new Cell().add(
            new Paragraph(label + "\n" + value).setFont(font).setFontSize(11).setFontColor(color))
            .setTextAlignment(TextAlignment.CENTER).setPadding(10));
    }

    private Cell cell(String text, com.itextpdf.kernel.font.PdfFont font) {
        return new Cell().add(new Paragraph(text).setFont(font).setFontSize(9));
    }

    private BigDecimal sum(List<Transaction> list, Transaction.TransactionType type) {
        return list.stream().filter(t -> t.getType() == type)
            .map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
