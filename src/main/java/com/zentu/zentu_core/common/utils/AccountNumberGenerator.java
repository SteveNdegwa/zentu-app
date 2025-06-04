package com.zentu.zentu_core.common.utils;

import com.zentu.zentu_core.billing.repository.AccountRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class AccountNumberGenerator {
	
	private final AtomicInteger counter = new AtomicInteger(1000000);
	
	@Autowired
	private AccountRepository accountRepository;
	
	@PostConstruct
	public void init() {
		Integer maxAccountNumber = accountRepository.findMaxAccountNumber();
		if (maxAccountNumber != null) {
			counter.set(maxAccountNumber + 1);
		}
	}
	
	public int generate() {
		return counter.getAndIncrement();
	}
}
