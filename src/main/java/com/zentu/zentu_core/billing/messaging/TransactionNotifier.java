package com.zentu.zentu_core.billing.messaging;

import com.zentu.zentu_core.billing.entity.Transaction;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransactionNotifier {
	
	private final SimpMessagingTemplate messagingTemplate;
	
	public TransactionNotifier(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}
	
	public void send(Transaction transaction) {
		messagingTemplate.convertAndSend("/topic/transactions", transaction);
	}
	
	public void sendToAlias(String alias, Transaction transaction) {
		messagingTemplate.convertAndSend("/topic/transactions/" + alias, transaction);
	}
}
