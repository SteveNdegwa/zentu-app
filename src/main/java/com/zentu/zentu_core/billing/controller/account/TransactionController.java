package com.zentu.zentu_core.billing.controller.account;
import com.zentu.zentu_core.billing.dto.WalletRequest;
import com.zentu.zentu_core.billing.service.account.AccountService;
import com.zentu.zentu_core.billing.service.transactions.TransactionExportService;
import com.zentu.zentu_core.common.utils.ResponseProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class TransactionController {
    private final AccountService accountService;
    
    private final TransactionExportService transactionExportService;
    
    @GetMapping("/export/excel")
    public ResponseEntity<InputStreamResource> exportTransactions(@RequestParam String alias) {
        InputStreamResource file = new InputStreamResource(transactionExportService.exportTransactionsByAlias(alias));
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions_" + alias + ".xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }
    
    @GetMapping("/export/pdf")
    public ResponseEntity<InputStreamResource> exportTransactionsAsPdf(@RequestParam String alias) {
        InputStreamResource file = new InputStreamResource(transactionExportService.exportTransactionsToPdf(alias));
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions_" + alias + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }
    
    @PostMapping("/topup")
    public ResponseEntity<?> topUp(@Valid @RequestBody WalletRequest request) {
        try {
            return accountService.topUp(request.getReceiptNumber(), request.getAlias(),  request.getAmount(), request.getAccountType());

        } catch (Exception e) {
            return new ResponseProvider("500.001", "Failed to topup Account").exception();
        }
    }

    @PostMapping("/deduct")
    public ResponseEntity<?> deduct(@RequestBody WalletRequest request) {
        try {
            return accountService.withdraw(request.getReceiptNumber(), request.getAlias(), request.getAmount(), request.getAccountType());
        } catch (Exception e) {
            return new ResponseProvider("500.001", "Failed to withdraw Account").exception();
        }
    }
}

