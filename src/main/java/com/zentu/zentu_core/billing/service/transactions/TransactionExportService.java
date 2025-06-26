package com.zentu.zentu_core.billing.service.transactions;

import com.zentu.zentu_core.billing.entity.Transaction;
import com.zentu.zentu_core.billing.repository.TransactionRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
	
	private String formatBigDecimal(BigDecimal value) {
		return value != null ? value.toPlainString() : "0.00";
	}
}
