package com.zentu.zentu_core.billing.intergrations;
import com.zentu.zentu_core.billing.utils.PhoneUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.Map;

import java.util.Base64;

public class MpesaInterface {
	
	@Value("${mpesa.consumer_key}")
	private String consumerKey;
	
	@Value("${mpesa.consumer_secret}")
	private String consumerSecret;
	
	@Value("${mpesa.shortcode}")
	private String shortcode;
	
	@Value("${mpesa.lipa_na_mpesa_shortcode}")
	private String lipaNaMpesaShortcode;
	
	@Value("${mpesa.shortcode_password}")
	private String shortcodePassword;
	
	@Value("${mpesa.callback_url}")
	private String callbackUrl;
	
	private final String apiBaseUrl = "https://sandbox.safaricom.co.ke/";
	
	public String getAccessToken() {
		String tokenUrl = apiBaseUrl + "oauth/v1/generate?grant_type=client_credentials";
		String authHeader = "Basic " + encodeCredentials(consumerKey, consumerSecret);
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", authHeader);
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Map> response = restTemplate.exchange(
				tokenUrl, HttpMethod.GET, new HttpEntity<>(headers), Map.class);
		
		return (String) response.getBody().get("access_token");
	}
	
	public String initiateSTKPush(String phoneNumber, double amount) {
		if (!PhoneUtils.validatePhoneNumber(phoneNumber, "254", 12)) {
			return "Invalid phone number";
		}
		
		String url = apiBaseUrl + "mpesa/stkpush/v1/processrequest";
		String token = getAccessToken();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		String password = Base64.getEncoder().encodeToString((lipaNaMpesaShortcode + shortcodePassword + timestamp).getBytes());
		
		String requestBody = String.format("""
                {
                    "BusinessShortCode":"%s",
                    "Password":"%s",
                    "Timestamp":"%s",
                    "TransactionType":"CustomerPayBillOnline",
                    "Amount":"%.0f",
                    "PartyA":"%s",
                    "PartyB":"%s",
                    "PhoneNumber":"%s",
                    "CallBackURL":"%s",
                    "AccountReference":"Ref12345",
                    "TransactionDesc":"Payment for goods"
                }
                """, lipaNaMpesaShortcode, password, timestamp, amount, phoneNumber, lipaNaMpesaShortcode, phoneNumber, callbackUrl);
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(requestBody, headers), String.class);
		return response.getBody();
	}
	
	public String queryTransactionStatus(String checkoutRequestID) {
		String url = apiBaseUrl + "mpesa/stkpushquery/v1/query";
		String token = getAccessToken();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		String password = Base64.getEncoder().encodeToString((lipaNaMpesaShortcode + shortcodePassword + timestamp).getBytes());
		
		String requestBody = String.format("""
                {
                    "BusinessShortCode":"%s",
                    "Password":"%s",
                    "Timestamp":"%s",
                    "CheckoutRequestID":"%s"
                }
                """, lipaNaMpesaShortcode, password, timestamp, checkoutRequestID);
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(requestBody, headers), String.class);
		
		return response.getBody();
	}
	
	private String encodeCredentials(String consumerKey, String consumerSecret) {
		return Base64.getEncoder().encodeToString((consumerKey + ":" + consumerSecret).getBytes());
	}
}
