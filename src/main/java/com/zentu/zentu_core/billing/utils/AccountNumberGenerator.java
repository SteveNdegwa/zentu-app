package com.zentu.zentu_core.billing.utils;
import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class AccountNumberGenerator {
	
	private final AtomicInteger counter = new AtomicInteger(1000000);
	
	public int generate() {
		return counter.getAndIncrement();
	}
}
