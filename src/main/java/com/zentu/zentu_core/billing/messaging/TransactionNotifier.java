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

	/**
	 * Send a transaction to the global transactions topic.
	 *
	 * @param transaction the transaction to send
	 */
	public void send(Transaction transaction) {
		String destination = "/topic/transactions";
		messagingTemplate.convertAndSend(destination, transaction);
		log.info("🔔 Sent transaction to {}", destination);
	}

	/**
	 * Send a transaction to a user-specific topic using alias.
	 *
	 * @param alias       the user or client alias
	 * @param transaction the transaction to send
	 */
	public void sendToAlias(String alias, Transaction transaction) {
		String destination = "/topic/transactions/" + alias;
		messagingTemplate.convertAndSend(destination, transaction);
		log.info("🔔 Sent transaction to {}", destination);
	}
}
