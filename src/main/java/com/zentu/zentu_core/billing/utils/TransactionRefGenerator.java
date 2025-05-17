package com.zentu.zentu_core.billing.utils;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class TransactionRefGenerator {
	private final List<Character> prefix = new ArrayList<>(List.of('A'));
	private String suffix = "0";
	private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final Random random = new Random();
	
	public String generate() {
		String randomString = randomAlphaNumeric(8);
		String transactionRef = getPrefix() + suffix + randomString;
		
		incrementSuffix();
		if (suffix.equals("0")) {
			incrementPrefix();
		}
		
		return transactionRef;
	}
	private void incrementSuffix() {
		StringBuilder sb = new StringBuilder(suffix);
		int i = sb.length() - 1;
		
		while (i >= 0) {
			char c = sb.charAt(i);
			if (c == 'Z') {
				sb.setCharAt(i, '0');
				i--;
			} else if (c == '9') {
				sb.setCharAt(i, 'A');
				break;
			} else {
				sb.setCharAt(i, (char)(c + 1));
				break;
			}
		}
		
		if (i < 0) {
			sb.insert(0, '0');
		}
		
		suffix = sb.toString();
	}
	private void incrementPrefix() {
		for (int i = prefix.size() - 1; i >= 0; i--) {
			char c = prefix.get(i);
			if (c == 'Z') {
				prefix.set(i, 'A');
			} else {
				prefix.set(i, (char)(c + 1));
				return;
			}
		}
		prefix.add(0, 'A');
	}
	private String getPrefix() {
		StringBuilder sb = new StringBuilder();
		for (char c : prefix) {
			sb.append(c);
		}
		return sb.toString();
	}
	private String randomAlphaNumeric(int length) {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(CHAR_POOL.charAt(random.nextInt(CHAR_POOL.length())));
		}
		return sb.toString();
	}
}
