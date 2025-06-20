package com.zentu.zentu_core.home.controller;
import com.zentu.zentu_core.common.utils.ResponseProvider;
import com.zentu.zentu_core.home.dto.HomeDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class HomeController {
	@PostMapping("/home")
	public ResponseEntity<?> createUser(@Valid @RequestBody HomeDTO request) {
		try {
			return new ResponseProvider("200.000", "User Information retrieve").success();
		} catch (Exception e) {
			return new ResponseProvider("500.001", "Failed to retrieve user Information").exception();
		}
	}
}
