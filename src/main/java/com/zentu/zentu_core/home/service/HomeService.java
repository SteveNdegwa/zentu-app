package com.zentu.zentu_core.home.service;

import com.zentu.zentu_core.home.dto.HomeDTO;
import org.springframework.http.ResponseEntity;

public interface HomeService {
		ResponseEntity<?> retrieveUserInformation(HomeDTO request);
}
