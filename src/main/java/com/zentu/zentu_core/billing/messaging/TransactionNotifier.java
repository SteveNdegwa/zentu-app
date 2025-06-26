package com.zentu.zentu_core.billing.messaging;

import com.zentu.zentu_core.billing.entity.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TransactionNotifier {
	
	private final SimpMessagingTemplate messagingTemplate;
	
	public TransactionNotifier(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}
	
	public void send(Transaction transaction) {
		messagingTemplate.convertAndSend("/topic/transactions", transaction);
		log.info("🔔 Sent transaction to /topic/transactions");
	}
	
	public void sendToAlias(String alias, Transaction transaction) {
		log.info("🔔 Sent transaction to /topic/transactions/" + alias);
		messagingTemplate.convertAndSend("/topic/transactions/" + alias, transaction);
		log.info("🔔 Sent transaction to /topic/transactions/" + alias);
	}
}

