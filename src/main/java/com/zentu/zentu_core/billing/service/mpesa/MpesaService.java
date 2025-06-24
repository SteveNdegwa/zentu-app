package com.zentu.zentu_core.billing.service.mpesa;
import com.zentu.zentu_core.billing.enums.AccountType;
import jakarta.persistence.EntityManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zentu.zentu_core.billing.entity.MpesaTransactionLog;
import com.zentu.zentu_core.billing.intergrations.DarajaClient;
import com.zentu.zentu_core.billing.service.account.AccountService;
import com.zentu.zentu_core.common.db.GenericCrudService;
import com.zentu.zentu_core.base.enums.State;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class MpesaService {

	@Autowired
	private DarajaClient darajaClient;

	@Autowired
	private GenericCrudService genericCrudService;

	@Autowired
	private AccountService accountService;


	private final ObjectMapper objectMapper = new ObjectMapper();

	public Map<String, Object> processStkCallback(Map<String, Object> data) {
		log.info("This is my Data : {}", data);
//		{Body={stkCallback={MerchantRequestID=f480-496e-87cb-a144f42b2c7b1933459, CheckoutRequestID=ws_CO_22052025131930057710956633, ResultCode=0, ResultDesc=The service request is processed successfully., CallbackMetadata={Item=[{Name=Amount, Value=1.0}, {Name=MpesaReceiptNumber, Value=TEM0QJRHL0}, {Name=TransactionDate, Value=20250522131941}, {Name=PhoneNumber, Value=254710956633}]}}}}
		
		Map<String, Object> body = safeCastToMap(data.get("Body"));
		if (body == null) return response("200.200.001", "Missing Body");
		Map<String, Object> callback = safeCastToMap(body.get("stkCallback"));
		if (callback == null) return response("200.200.001", "Missing stkCallback");
		String checkoutId = (String) callback.get("CheckoutRequestID");
		Object resultCodeObj = callback.get("ResultCode");
		String resultDesc = (String) callback.getOrDefault("ResultDesc", "No description");
		if (checkoutId == null || resultCodeObj == null) {
			return response("200.200.001", "Missing callback data");
		}
		Optional<MpesaTransactionLog> transactionOpt = findTransactionByCheckoutRequestId(checkoutId);
		if (transactionOpt.isEmpty()) {
			return response("200.200.001", "Transaction not found");
		}
		MpesaTransactionLog transaction = transactionOpt.get();
		try {
			String jsonResponse = objectMapper.writeValueAsString(data);
			transaction.setResponse(jsonResponse);
		} catch (Exception e) {
			log.info("Failed to convert response to JSON: {}", e.getMessage());
			e.printStackTrace();
			transaction.setResponse(null);
		}
		int resultCode = (resultCodeObj instanceof Number) ? ((Number) resultCodeObj).intValue() : -1;
		if (resultCode == 0) {
			Map<String, Object> metadataMap = new HashMap<>();
			Object metadataObj = callback.get("CallbackMetadata");
			if (metadataObj instanceof Map<?, ?> metadataRaw) {
				Object itemObj = metadataRaw.get("Item");
				if (itemObj instanceof List<?> items) {
					for (Object item : items) {
						if (item instanceof Map<?, ?> itemMap) {
							String name = (String) itemMap.get("Name");
							Object value = itemMap.get("Value");
							if (name != null) {
								metadataMap.put(name, value);
							}
						}
					}
				}
			}
			String communityAlias = transactionOpt.get().getCommunityAlias();
			log.info("Alias here ++++++++++++++++++++{}", communityAlias);
			String receipt = (String) metadataMap.get("MpesaReceiptNumber");
			log.info("Receipt here ++++++++++++++++++++{}", receipt);
			BigDecimal amount = metadataMap.get("Amount") instanceof Number
					? new BigDecimal(((Number) metadataMap.get("Amount")).toString())
					: null;
			log.info("Amount here ++++++++++++++++{}", amount);
			log.info("Phone here+++++++++++++++++++++++{}", metadataMap.get("PhoneNumber"));
			Object phoneObj = metadataMap.get("PhoneNumber");
			String phone = (phoneObj != null) ? phoneObj.toString() : null;
			
			if (phone == null || amount == null) {
				log.warn("Missing phone or amount: phone={}, amount={}", phone, amount);
				return response("200.200.002", "Missing phone or amount");
			}
			
			log.info("Phone here: {}", phone);
			log.info("Amount here: {}", amount);
			AccountType accountType = AccountType.USER;
			var processTopUp = accountService.topUp(receipt, communityAlias, amount, accountType);
			log.info("processTopUp here: {}", processTopUp);
			genericCrudService.updateFields(MpesaTransactionLog.class, transaction.getId(), Map.of(
					"receipt", receipt,
					"phoneNumber", phone,
					"state", State.COMPLETED,
					"response", transaction.getResponse()
			));
			return response("200.200.001", "Transaction Successful");
		} else {
			return response("200.200.001", resultDesc);
		}
	}

	public Map<String, Object> processConfirmation(Map<String, Object> data) {
		String communityAlias = (String) data.get("BillRefNumber");
		String transId = (String) data.get("TransID");
		double amount = Double.parseDouble(data.get("TransAmount").toString());
		String phoneNumber = data.get("MSISDN").toString();
		AccountType accountType = AccountType.COMMUNITY;
		accountService.topUp(transId, communityAlias, BigDecimal.valueOf(amount), accountType);
		MpesaTransactionLog transaction = MpesaTransactionLog.builder()
				.communityAlias(communityAlias)
				.phoneNumber(phoneNumber)
				.receipt(transId)
				.amount(amount)
				.state(State.COMPLETED)
				.response(convertMapToJson(data))
				.build();

		genericCrudService.create(transaction);

		return response("200.200.001", data);
	}

	public Map<String, Object> processValidation(Map<String, Object> data) {
		return response("200.200.001", data);
	}

	public Map<String, Object> initiateStkPush(Map<String, Object> data) {
		if (Stream.of("phone_number", "amount", "account_reference", "community_alias", "transaction_desc")
				.anyMatch(key -> !data.containsKey(key))) {
			return response("500.200.001", "Missing required fields");
		}

		Map<String, Object> result = darajaClient.stkPush(
				data.get("phone_number").toString(),
				Double.parseDouble(data.get("amount").toString()),
				"https://df76-197-232-96-207.ngrok-free.app/mpesa/callback",
				data.get("account_reference").toString(),
				data.get("transaction_desc").toString()
		);

		String checkoutRequestId = result.get("CheckoutRequestID").toString();
		String communityAlias = data.get("community_alias").toString();

		MpesaTransactionLog transaction = MpesaTransactionLog.builder()
				.checkoutRequestId(checkoutRequestId)
				.communityAlias(communityAlias)
				.phoneNumber(data.get("phone_number").toString())
				.state(State.COMPLETED)
				.response(convertMapToJson(result))
				.build();

		genericCrudService.create(transaction);

		return response("200.200.001", data);
	}

	private Map<String, Object> response(String code, Object data) {
		Map<String, Object> resp = new HashMap<>();
		resp.put("code", code);
		resp.put("data", data);
		return resp;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> safeCastToMap(Object obj) {
		if (obj instanceof Map<?, ?> map) {
			Map<String, Object> result = new HashMap<>();
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				if (entry.getKey() instanceof String) {
					result.put((String) entry.getKey(), entry.getValue());
				}
			}
			return result;
		}
		return null;
	}

	private String convertMapToJson(Map<String, Object> map) {
		try {
			return objectMapper.writeValueAsString(map);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	@PersistenceContext
	private EntityManager entityManager;

	private Optional<MpesaTransactionLog> findTransactionByCheckoutRequestId(String checkoutRequestId) {
		String jpql = "SELECT m FROM MpesaTransactionLog m WHERE m.checkoutRequestId = :checkoutRequestId";
		TypedQuery<MpesaTransactionLog> query = entityManager.createQuery(jpql, MpesaTransactionLog.class);
		query.setParameter("checkoutRequestId", checkoutRequestId);

		try {
			MpesaTransactionLog transaction = query.getSingleResult();
			return Optional.of(transaction);
		} catch (jakarta.persistence.NoResultException ex) {
			return Optional.empty();
		}
	}
}
