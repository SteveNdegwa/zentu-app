package com.zentu.zentu_core.base.repository;

import com.zentu.zentu_core.base.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OtpRepository extends JpaRepository<Otp, UUID> {
    Optional<Otp> findByPhoneNumberAndOtpAndVerifiedFalse(String credential, String otp);
}
