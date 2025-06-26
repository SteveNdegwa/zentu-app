package com.zentu.zentu_core.common.utils;

import java.math.BigDecimal;

public class ChargeCalculator {
	
	/**
	 * Calculates the charge based on the provided amount using predefined brackets.
	 *
	 * @param amount the transaction amount
	 * @return the charge as BigDecimal
	 */
	public static BigDecimal calculateCharge(BigDecimal amount) {
		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
		
		if (amount.compareTo(BigDecimal.valueOf(50)) <= 0) return BigDecimal.valueOf(1);
		else if (amount.compareTo(BigDecimal.valueOf(100)) <= 0) return BigDecimal.valueOf(5);
		else if (amount.compareTo(BigDecimal.valueOf(500)) <= 0) return BigDecimal.valueOf(10);
		else if (amount.compareTo(BigDecimal.valueOf(1000)) <= 0) return BigDecimal.valueOf(15);
		else if (amount.compareTo(BigDecimal.valueOf(3000)) <= 0) return BigDecimal.valueOf(20);
		else if (amount.compareTo(BigDecimal.valueOf(5000)) <= 0) return BigDecimal.valueOf(25);
		else if (amount.compareTo(BigDecimal.valueOf(7000)) <= 0) return BigDecimal.valueOf(30);
		else if (amount.compareTo(BigDecimal.valueOf(10000)) <= 0) return BigDecimal.valueOf(35);
		else if (amount.compareTo(BigDecimal.valueOf(20000)) <= 0) return BigDecimal.valueOf(50);
		else if (amount.compareTo(BigDecimal.valueOf(50000)) <= 0) return BigDecimal.valueOf(100);
		else if (amount.compareTo(BigDecimal.valueOf(100000)) <= 0) return BigDecimal.valueOf(500);
		else if (amount.compareTo(BigDecimal.valueOf(200000)) <= 0) return BigDecimal.valueOf(1000);
		else if (amount.compareTo(BigDecimal.valueOf(300000)) <= 0) return BigDecimal.valueOf(1500);
		else if (amount.compareTo(BigDecimal.valueOf(500000)) <= 0) return BigDecimal.valueOf(2000);
		else if (amount.compareTo(BigDecimal.valueOf(700000)) <= 0) return BigDecimal.valueOf(3000);
		else if (amount.compareTo(BigDecimal.valueOf(1000000)) <= 0) return BigDecimal.valueOf(4000);
		else return BigDecimal.valueOf(5000);
	}
}
