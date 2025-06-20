package com.zentu.zentu_core.home.service;

import com.zentu.zentu_core.billing.entity.Account;
import com.zentu.zentu_core.billing.enums.AccountType;
import com.zentu.zentu_core.billing.repository.AccountRepository;
import com.zentu.zentu_core.common.utils.AccountNumberGenerator;
import com.zentu.zentu_core.common.utils.ResponseProvider;
import com.zentu.zentu_core.home.dto.HomeDTO;
import com.zentu.zentu_core.user.service.util.UserServiceSync;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeServiceImpl implements HomeService{
	private final UserServiceSync userServiceSync;
	private final AccountRepository accountRepository;
	private final AccountNumberGenerator accountNumberGenerator;
	public ResponseEntity<?> retrieveUserInformation(HomeDTO request) {
		Map<String, Object> userData = new HashMap<>();
		if (request.getAlias() != null) userData.put("alias", request.getAlias());
		Map<String, Object> response = userServiceSync.sync("sync_user_profile", userData);
		Map<String, Object> data = new HashMap<>();
		data.put("code", response.get("code"));
		data.put("message", response.get("message"));
		
		if ("200.000".equals(response.get("code"))) {
			Object messageObj = response.get("message");
			if (messageObj instanceof Map<?, ?>) {
				Map<?, ?> messageMap = (Map<?, ?>) messageObj;
				
				Object userId = messageMap.get("user");
				if (userId instanceof String userIdStr) {
					log.info("User created with ID: {}", userIdStr);
				}
				Object aliasObj = messageMap.get("alias");
				String userAlias = aliasObj instanceof String ? (String) aliasObj : null;
				Optional<Account> account = accountRepository.findByAlias(request.getAlias());
				if (account.isPresent()) {
					response.put("available", account.map(Account::getAvailable).orElse(BigDecimal.ZERO));
				} else {
					log.warn("No account found for alias: {}", request.getAlias());
					response.put("available", BigDecimal.ZERO);
				}
				return new ResponseProvider(data).success();
			} else {
				log.warn("Expected 'message' to be a Map, got: {}", messageObj.getClass().getSimpleName());
				return new ResponseProvider(data).exception();
			}
		} else {
			return new ResponseProvider(data).exception();
		}
	}
}
