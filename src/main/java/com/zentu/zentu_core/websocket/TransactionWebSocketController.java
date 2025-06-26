package com.zentu.zentu_core.websocket;


import com.zentu.zentu_core.billing.entity.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class TransactionWebSocketController {

    @MessageMapping("/transactions")
    @SendTo("/topic/transactions")
    public Transaction send(Transaction transaction) {
        log.info("Received transaction: " + transaction);
        return transaction;
    }
}
