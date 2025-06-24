package com.zentu.zentu_core.billing.entity;

import com.zentu.zentu_core.base.entity.BaseEntity;
import com.zentu.zentu_core.base.enums.State;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "mpesa_transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MpesaTransactionLog extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private UUID id;
	
	@Column(name = "checkout_request_id", unique = true)
	private String checkoutRequestId;
	
	@Column(name = "alias")
	private String communityAlias;
	
	@Column(name = "source_key")
	private String sourceKey;
	
	@Column(name = "termination_key")
	private String terminationKey;
	
	@Column(name = "description", columnDefinition = "TEXT")
	private String description;
	
	@Column(name = "phone_number")
	private String phoneNumber;
	
	@Column(name = "receipt")
	private String receipt;
	
	@Column(name = "amount")
	private Double amount;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", precision = 25, scale = 2, nullable = false)
	private State state = State.ACTIVE;
	
	@Column(name = "response", columnDefinition = "TEXT")
	private String response;
	
	
}

