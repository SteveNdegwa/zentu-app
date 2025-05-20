package com.zentu.zentu_core.billing.service.mpesa;

import com.zentu.zentu_core.billing.intergrations.DarajaClient;
import com.zentu.zentu_core.billing.service.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MpesaService {
	@Autowired
	private DarajaClient darajaClient;
//	@Autowired private MpesaTransactionLogService transactionService;
	@Autowired private AccountService accountService;
	public Map<String, Object> processStkCallback(Map<String, Object> data) {
		Map<String, Object> callback = (Map<String, Object>) ((Map<String, Object>) data.get("Body")).get("stkCallback");
		String checkoutId = (String) callback.get("CheckoutRequestID");
		Integer resultCode = (Integer) callback.get("ResultCode");
		String resultDesc = (String) callback.getOrDefault("ResultDesc", "No description");
		
		if (checkoutId == null || callback == null) {
			return response("200.200.001", "Missing callback data");
		}
//		Optional<MpesaTransaction> transactionOpt = transactionService.findByCheckoutRequestId(checkoutId);
//		if (transactionOpt.isEmpty()) {
//			return response("200.200.001", "Transaction not found");
//		}
//		MpesaTransaction transaction = transactionOpt.get();
		
		if (resultCode == 0) {
			List<Map<String, Object>> metadataItems = (List<Map<String, Object>>) ((Map<String, Object>) callback.get("CallbackMetadata")).get("Item");
			Map<String, Object> metadata = metadataItems.stream().collect(Collectors.toMap(
					item -> (String) item.get("Name"),
					item -> item.get("Value")
			));
			
			String receipt = (String) metadata.get("MpesaReceiptNumber");
			Double amount = (Double) metadata.get("Amount");
			String phone = (String) metadata.get("PhoneNumber");
			
//			transactionService.updateTransaction(transaction.getId(), data, stateService.getByName("Completed"), receipt, phone);
			
//			accountService.topUp(transaction.getRemoteCode(), receipt, amount);
			
			return response("200.200.001", "Transaction Successful");
		} else {
//			transactionService.updateTransaction(transaction.getId(), data, stateService.getByName("Failed"), null, null);
			return response("200.200.001", resultDesc);
		}
	}
	
	public Map<String, Object> processConfirmation(Map<String, Object> data) {
		String remoteCode = (String) data.get("BillRefNumber");
		String transId = (String) data.get("TransID");
		Double amount = Double.valueOf(data.get("TransAmount").toString());
		
//		accountService.topUp(phoneNumber, transId, amount);
//		transactionService.create(data.get("MSISDN").toString(), data, transId, stateService.getByName("Completed"));
		
		return response("200.200.001", data);
	}
	
	public Map<String, Object> processValidation(Map<String, Object> data) {
		return response("200.200.001", data);
	}
	
	public Map<String, Object> initiateStkPush(Map<String, Object> data) {
		if (Stream.of("phone_number", "amount", "account_reference", "remote_code", "transaction_desc").anyMatch(key -> !data.containsKey(key))) {
			return response("500.200.001", "Missing required fields");
		}
		
		Map<String, Object> result = darajaClient.stkPush(
				data.get("phone_number").toString(),
				Double.parseDouble(data.get("amount").toString()),
				System.getenv("DARAJA_CALLBACK_URL"),
				data.get("account_reference").toString(),
				data.get("transaction_desc").toString()
		);
		
		String checkoutRequestId = result.get("CheckoutRequestID").toString();
		String remoteCode = data.get("remote_code").toString();
//
//		MpesaTransaction transaction = transactionService.create(
//				result,
//				remoteCode,
//				remoteCode,
//				result,
//				data.get("phone_number").toString(),
//				checkoutRequestId,
//				stateService.getByName("Completed")
//		);
//
//		if (transaction == null) {
//			return response("500.200.001", "Failed to create transaction");
//		}
		
		return response("200.200.001", data);
	}
	
	private Map<String, Object> response(String code, Object data) {
		Map<String, Object> resp = new HashMap<>();
		resp.put("code", code);
		resp.put("data", data);
		return resp;
	}
}
