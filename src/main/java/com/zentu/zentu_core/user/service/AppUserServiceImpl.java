package com.zentu.zentu_core.user.service;
import com.zentu.zentu_core.billing.entity.Account;
import com.zentu.zentu_core.billing.repository.AccountRepository;
import com.zentu.zentu_core.common.utils.AccountNumberGenerator;
import com.zentu.zentu_core.common.utils.ResponseProvider;
import com.zentu.zentu_core.user.dto.*;
import com.zentu.zentu_core.user.entity.AppUser;
import com.zentu.zentu_core.user.entity.UserSession;
import com.zentu.zentu_core.user.repository.AppUserRepository;
import com.zentu.zentu_core.user.repository.UserSessionRepository;
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
	
	private final AppUserRepository userRepository;
	private final UserSessionRepository sessionRepository;
	private final UserServiceSync userServiceSync;
	private final AccountRepository accountRepository;
	private final AccountNumberGenerator accountNumberGenerator;
	public ResponseEntity<?> createAppUser(CreateAppUserDto request) {
		Map<String, Object> userData = new HashMap<>();
		if (request.getRole() != null) userData.put("role", request.getRole());
		if (request.getUsername() != null) userData.put("username", request.getUsername());
		if (request.getFirstName() != null) userData.put("first_name", request.getFirstName());
		if (request.getLastName() != null) userData.put("last_name", request.getLastName());
		if (request.getOtherName() != null) userData.put("other_name", request.getOtherName());
		if (request.getPhoneNumber() != null) userData.put("phone_number", request.getPhoneNumber());
		if (request.getEmail() != null) userData.put("email", request.getEmail());
		if (request.getApp() != null) userData.put("app", request.getApp());
		userData.put("set_random_password", request.isSetRandomPassword());
		if (!request.isSetRandomPassword()) {
			if (request.getNewPassword() != null) userData.put("new_password", request.getNewPassword());
			if (request.getConfirmPassword() != null) userData.put("confirm_password", request.getConfirmPassword());
		}
		userData.put("change_password_next_login", request.isChangePasswordNextLogin());
		log.info("Creating user with data: {}", userData);
		log.info("Creating user with data: {}", userData);
		Map<String, Object> response = userServiceSync.sync("register", userData);
		log.info("User creation response: {}", response);
		Map<String, Object> data = new HashMap<>();
		data.put("code", response.get("code"));
		data.put("message", response.get("message"));
		if ("200.000".equals(response.get("code"))) {
			AppUser user = new AppUser();
			Object messageObj = response.get("message");
			if (messageObj instanceof Map<?, ?> messageMap) {
				Object userId = messageMap.get("user");
				if (userId instanceof String userIdStr) {
					user.setUserId(userIdStr);
				}
			}
			userRepository.save(user);
			Account account = new Account();
			account.setAccountNumber(accountNumberGenerator.generate());
			accountRepository.save(account);
			return new ResponseProvider(data).success();
		} else {
			return new ResponseProvider(data).exception();
		}
	}
	
	public ResponseEntity<?> loginUser(LoginRequest request) {
		Map<String, Object> loginData = new HashMap<>();
		if (request.getUsername() != null) loginData.put("username", request.getUsername());
		if (request.getApp() != null) loginData.put("app", request.getApp());
		if (request.getPassword() != null) loginData.put("password", request.getPassword());
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

	public ResponseEntity<?> verifyPhoneNumber(VerifyPhoneNumberRequest request) {
		Map<String, Object> verifyData = new HashMap<>();
		if (request.getPhoneNumber() != null) verifyData.put("phone_number", request.getPhoneNumber());
		Map<String, Object> response = userServiceSync.sync("verify_phone_number", verifyData);
		Map<String, Object> data = new HashMap<>();
		data.put("code", "200.000");
		data.put("message", response.get("message"));
		// Todo Send Notification to user
		return new ResponseProvider(data).success();
	}

	public ResponseEntity<?> verifyOtp(VerifyOtpRequest request) {
		Map<String, Object> verifyData = new HashMap<>();
		if (request.getPhoneNumber() != null) verifyData.put("phone_number", request.getPhoneNumber());
		if (request.getOtp() != null) verifyData.put("otp", request.getOtp());
		Map<String, Object> response = userServiceSync.sync("verify_phone_number", verifyData);
		Map<String, Object> data = new HashMap<>();
		data.put("code", "200.000");
		data.put("message", response.get("message"));
		return new ResponseProvider(data).success();
	}
	
	public void logoutUser(String userId) {
		AppUser user = userRepository.findByUserId(userId).orElseThrow();
		sessionRepository.save(new UserSession(null, user, "logout", null));
		userServiceSync.sync("logout", Map.of("user_id", userId));
	}
	
	public void disableUser(String userId) {
		AppUser user = userRepository.findByUserId(userId).orElseThrow();
		user.setIsActive(false);
		userRepository.save(user);
		userServiceSync.sync("disable", Map.of("user_id", userId));
	}
}
