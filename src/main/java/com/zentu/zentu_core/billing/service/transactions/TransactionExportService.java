package com.zentu.zentu_core.billing.service.transactions;

import com.zentu.zentu_core.billing.entity.Transaction;
import com.zentu.zentu_core.billing.repository.TransactionRepository;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionExportService {
	
	private final TransactionRepository transactionRepository;
	
	public TransactionExportService(TransactionRepository transactionRepository) {
		this.transactionRepository = transactionRepository;
	}
	
	public ByteArrayInputStream exportTransactionsByAlias(String alias) {
		List<Transaction> transactions = transactionRepository.findByAlias(alias);
		
		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet("Transactions");
			
			Row header = sheet.createRow(0);
			String[] columns = {"Receipt", "Reference", "Alias", "Account Type", "Amount", "Charge", "Balance", "Transaction Type", "Status", "Created"};
			for (int i = 0; i < columns.length; i++) {
				header.createCell(i).setCellValue(columns[i]);
			}
			
			int rowIdx = 1;
			for (Transaction tx : transactions) {
				Row row = sheet.createRow(rowIdx++);
				row.createCell(0).setCellValue(tx.getReceiptNumber());
				row.createCell(1).setCellValue(tx.getInternalReference());
				row.createCell(2).setCellValue(tx.getAlias());
				row.createCell(3).setCellValue(tx.getAccountType() != null ? tx.getAccountType().name() : "");
				row.createCell(4).setCellValue(formatBigDecimal(tx.getAmount()));
				row.createCell(5).setCellValue(formatBigDecimal(tx.getCharge()));
				row.createCell(6).setCellValue(formatBigDecimal(tx.getBalance()));
				row.createCell(7).setCellValue(tx.getTransactionType().name());
				row.createCell(8).setCellValue(tx.getStatus().name());
				row.createCell(9).setCellValue(tx.getCreatedAt() != null ? tx.getCreatedAt().toString() : "");
			}
			
			workbook.write(out);
			return new ByteArrayInputStream(out.toByteArray());
			
		} catch (Exception e) {
			throw new RuntimeException("Failed to export transactions to Excel", e);
		}
	}
	
	public ByteArrayInputStream exportTransactionsToPdf(String alias) {
		List<Transaction> transactions = transactionRepository.findByAlias(alias);
		
		try (PDDocument document = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			PDRectangle rotated = new PDRectangle(PDRectangle.LETTER.getHeight(), PDRectangle.LETTER.getWidth());
			PDPage page = new PDPage(rotated);
			document.addPage(page);
			
			PDFont font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
			PDPageContentStream contentStream = new PDPageContentStream(document, page);
			
			float margin = 40;
			float yStart = rotated.getHeight() - margin;
			float xStart = margin;
			float leading = 14f;
			
			contentStream.beginText();
			contentStream.setFont(font, 16);
			contentStream.newLineAtOffset(xStart, yStart);
			contentStream.showText("Transactions for Alias: " + alias);
			contentStream.endText();
			
			float yPos = yStart - 30;
			contentStream.setFont(font, 10);
			contentStream.beginText();
			contentStream.newLineAtOffset(xStart, yPos);
			
			String[] headers = {"Receipt", "Reference", "Amount", "Charge", "Balance", "Type", "Status", "Created"};
			for (String header : headers) {
				contentStream.showText(padRight(header, 20));
			}
			contentStream.newLineAtOffset(0, -leading);
			
			for (Transaction tx : transactions) {
				String[] row = {
						tx.getReceiptNumber(),
						tx.getInternalReference(),
						formatBigDecimal(tx.getAmount()),
						formatBigDecimal(tx.getCharge()),
						formatBigDecimal(tx.getBalance()),
						tx.getTransactionType().name(),
						tx.getStatus().name(),
						tx.getCreatedAt() != null ? tx.getCreatedAt().toString() : ""
				};
				
				for (String col : row) {
					contentStream.showText(padRight(col, 20));
				}
				contentStream.newLineAtOffset(0, -leading);
			}
			
			contentStream.endText();
			contentStream.close();
			
			document.save(out);
			return new ByteArrayInputStream(out.toByteArray());
			
		} catch (Exception e) {
			throw new RuntimeException("Failed to export transactions to PDF", e);
		}
	}
	
	private String formatBigDecimal(BigDecimal value) {
		return value != null ? value.toPlainString() : "0.00";
	}
	
	private String padRight(String text, int length) {
		if (text == null) return " ".repeat(length);
		return String.format("%-" + length + "s", text.length() > length ? text.substring(0, length - 1) + "…" : text);
	}
}
