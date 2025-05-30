package com.zentu.zentu_core.billing.controller.account;
import com.zentu.zentu_core.billing.dto.WalletRequest;
import com.zentu.zentu_core.billing.service.account.AccountService;
import com.zentu.zentu_core.common.utils.ResponseProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class TransactionController {
    private final AccountService accountService;
    @PostMapping("/topup")
    public ResponseEntity<?> topUp(@Valid @RequestBody WalletRequest request) {
        try {
            return accountService.topUp(request.getReceiptNumber(), request.getGroupAlias(), request.getPhoneNumber(), request.getAmount());

        } catch (Exception e) {
            return new ResponseProvider("500.000.001", "Failed to topup Account").exception();
        }
    }

    @PostMapping("/deduct")
    public ResponseEntity<?> deduct(@RequestBody WalletRequest request) {
        try {
            return accountService.withdraw(request.getReceiptNumber(), request.getPhoneNumber(), request.getGroupAlias(), request.getAmount());
        } catch (Exception e) {
            return new ResponseProvider("500.000.001", "Failed to topup Account").exception();
        }
    }
}

