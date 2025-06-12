package com.zentu.zentu_core.billing.intergrations;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.Map;

import java.util.Base64;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

@Component
public class DarajaClient {

	private final String consumerKey;
	private final String consumerSecret;
	private final String shortcode;
	private final String passkey;
	private final String baseUrl;


	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	public DarajaClient(
			@Value("${daraja.consumer.key}") String consumerKey,
			@Value("${daraja.consumer.secret}") String consumerSecret,
			@Value("${daraja.business.shortcode}") String shortcode,
			@Value("${daraja.business.passkey}") String passkey,
			@Value("${daraja.base.url}") String baseUrl
	) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.shortcode = shortcode;
		this.passkey = passkey;
		this.baseUrl = baseUrl;
		this.restTemplate = new RestTemplate();
		this.objectMapper = new ObjectMapper();
	}


	private String generateToken() {
		String url = baseUrl + "/oauth/v1/generate?grant_type=client_credentials";
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(consumerKey, consumerSecret);
		HttpEntity<Void> entity = new HttpEntity<>(headers);
		ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
		if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
			return (String) response.getBody().get("access_token");
		} else {
			throw new RuntimeException("Failed to generate token from Daraja API");
		}
	}

	private HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		String token = generateToken();
		headers.setBearerAuth(token);
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	public Map<String, Object> stkPush(String phoneNumber, double amount, String callbackUrl, String accountReference, String transactionDesc) {
		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		String passwordStr = shortcode + passkey + timestamp;
		String password = Base64.getEncoder().encodeToString(passwordStr.getBytes(StandardCharsets.UTF_8));

		Map<String, Object> payload = new HashMap<>();
		payload.put("BusinessShortCode", shortcode);
		payload.put("Password", password);
		payload.put("Timestamp", timestamp);
		payload.put("TransactionType", "CustomerPayBillOnline");
		payload.put("Amount", amount);
		payload.put("PartyA", phoneNumber);
		payload.put("PartyB", shortcode);
		payload.put("PhoneNumber", phoneNumber);
		payload.put("CallBackURL", callbackUrl);
		payload.put("AccountReference", accountReference);
		payload.put("TransactionDesc", transactionDesc);

		String url = baseUrl + "/mpesa/stkpush/v1/processrequest";

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, getHeaders());
		ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

		return response.getBody();
	}

	public Map<String, Object> registerC2bUrls(String validationUrl, String confirmationUrl, String responseType) {
		Map<String, Object> payload = new HashMap<>();
		payload.put("ShortCode", shortcode);
		payload.put("ResponseType", responseType);
		payload.put("ConfirmationURL", confirmationUrl);
		payload.put("ValidationURL", validationUrl);

		String url = baseUrl + "/mpesa/c2b/v1/registerurl";

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, getHeaders());
		ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

		return response.getBody();
	}

	public Map<String, Object> transactionStatus(String transactionId, String partyA, String identifierType,
	                                             String remarks, String occasion) {
		Map<String, Object> payload = new HashMap<>();
		payload.put("Initiator", "testapi");
		payload.put("SecurityCredential", "SECURITY_CREDENTIAL");
		payload.put("CommandID", "TransactionStatusQuery");
		payload.put("TransactionID", transactionId);
		payload.put("PartyA", partyA);
		payload.put("IdentifierType", identifierType);
		payload.put("ResultURL", "https://zentu.com/path/to/result");
		payload.put("QueueTimeOutURL", "https://zentu.com/path/to/timeout");
		payload.put("Remarks", remarks);
		payload.put("Occasion", occasion);

		String url = baseUrl + "/mpesa/transactionstatus/v1/query";

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, getHeaders());
		ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

		return response.getBody();
	}

	public Map<String, Object> reversal(String transactionId, double amount, String receiverParty,
	                                    String remarks, String occasion) {
		Map<String, Object> payload = new HashMap<>();
		payload.put("Initiator", "testapi");
		payload.put("SecurityCredential", "SECURITY_CREDENTIAL");
		payload.put("CommandID", "TransactionReversal");
		payload.put("TransactionID", transactionId);
		payload.put("Amount", amount);
		payload.put("ReceiverParty", receiverParty);
		payload.put("ReceiverIdentifierType", "11");
		payload.put("ResultURL", "https://spinmobile.co/path/to/result");
		payload.put("QueueTimeOutURL", "https://spinmobile.co/path/to/timeout");
		payload.put("Remarks", remarks);
		payload.put("Occasion", occasion);

		String url = baseUrl + "/mpesa/reversal/v1/request";

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, getHeaders());
		ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

		return response.getBody();
	}
}
