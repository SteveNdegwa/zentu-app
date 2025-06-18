package com.zentu.zentu_core.user.service;
import com.zentu.zentu_core.billing.entity.Account;
import com.zentu.zentu_core.billing.enums.AccountType;
import com.zentu.zentu_core.billing.repository.AccountRepository;
import com.zentu.zentu_core.common.utils.AccountNumberGenerator;
import com.zentu.zentu_core.common.utils.ResponseProvider;
import com.zentu.zentu_core.user.dto.*;
import com.zentu.zentu_core.user.service.util.UserServiceSync;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserServiceImpl implements AppUserService {
	
	private final UserServiceSync userServiceSync;
	private final AccountRepository accountRepository;
	private final AccountNumberGenerator accountNumberGenerator;
	public ResponseEntity<?> createAppUser(CreateAppUserDto request) {
		Map<String, Object> userData = new HashMap<>();
		if (request.getFirstName() != null) userData.put("first_name", request.getFirstName());
		if (request.getLastName() != null) userData.put("last_name", request.getLastName());
		if (request.getPhoneNumber() != null) userData.put("phone_number", request.getPhoneNumber());
		if (request.getPhoneNumber() != null) {
			String firstName = request.getFirstName() != null ? request.getFirstName() : "";
			String lastName = request.getLastName() != null ? request.getLastName() : "";
			userData.put("username", request.getPhoneNumber() + firstName + lastName);
		}
		if (request.getApp() != null) userData.put("app", request.getApp());
		userData.put("role", "Customer");
		if (request.getPin() != null) userData.put("pin", request.getPin());
		userData.put("change_password_next_login", false);
		log.info("Creating user with data: {}", userData);
		log.info("Creating user with data: {}", userData);
		Map<String, Object> response = userServiceSync.sync("register", userData);
		log.info("User creation response: {}", response);
		Map<String, Object> data = new HashMap<>();
		data.put("code", response.get("code"));
		data.put("message", response.get("message"));
		if ("200.000".equals(response.get("code"))) {
			Object messageObj = response.get("message");
			if (messageObj instanceof Map<?, ?> messageMap) {
				Object userId = messageMap.get("user");
				if (userId instanceof String userIdStr) {
					log.info("User created with ID: {}", userIdStr);
				}
			}
			Account account = new Account();
			account.setAccountNumber(accountNumberGenerator.generate());
			account.setAccountType(AccountType.USER);
			account.setAlias(request.getPhoneNumber());
			accountRepository.save(account);
			return new ResponseProvider(data).success();
		} else {
			return new ResponseProvider(data).exception();
		}
	}
	
	public ResponseEntity<?> loginUser(LoginRequest request) {
		Map<String, Object> loginData = new HashMap<>();
		if (request.getPhoneNumber() != null) loginData.put("username", request.getPhoneNumber());
		if (request.getApp() != null) loginData.put("app", request.getApp());
		if (request.getPin() != null) loginData.put("password", request.getPin());
		Map<String, Object> response = userServiceSync.sync("token", loginData);
		Map<String, Object> data = new HashMap<>();
		data.put("code", "200.000");
		data.put("message", response.get("message"));
		return new ResponseProvider(data).success();
	}
	
	public ResponseEntity<?> retrieveProfile(RetrieveProfileRequest request) {
		Map<String, Object> loginData = new HashMap<>();
		if (request.getUser() != null) loginData.put("user", request.getUser());
		Map<String, Object> response = userServiceSync.sync("sync_user_profile", loginData);
		Map<String, Object> data = new HashMap<>();
		data.put("code", "200.000");
		data.put("message", response.get("message"));
		return new ResponseProvider(data).success();
	}
	
	
	public ResponseEntity<?> checkPhoneNumber(CheckPhoneNumber request) {
		Map<String, Object> checkPhoneData = new HashMap<>();
		if (request.getPhoneNumber() != null) checkPhoneData.put("phone_number", request.getPhoneNumber());
		Map<String, Object> response = userServiceSync.sync("verify_phone_number", checkPhoneData);
		Map<String, Object> data = new HashMap<>();
		data.put("code", response.get("code"));
		data.put("message", response.get("message"));
		return new ResponseProvider(data).success();
	}
	
	public ResponseEntity<?> verifyOtp(VerifyOtp request) {
		Map<String, Object> otpData = new HashMap<>();
		if (request.getPhoneNumber() != null) otpData.put("phone_number", request.getPhoneNumber());
		if (request.getOtp() != null) otpData.put("otp", request.getOtp());
		Map<String, Object> response = userServiceSync.sync("verify_otp", otpData);
		Map<String, Object> data = new HashMap<>();
		data.put("code", response.get("code"));
		data.put("message", response.get("message"));
		return new ResponseProvider(data).success();
	}
	
	
	public void logoutUser(String userId) {
		userServiceSync.sync("logout", Map.of("user_id", userId));
	}
	
	public void disableUser(String userId) {
		userServiceSync.sync("disable", Map.of("user_id", userId));
	}
}
