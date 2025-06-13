package com.zentu.zentu_core.service.repository;

import com.zentu.zentu_core.service.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceReviewRepository extends JpaRepository<ServiceReview, UUID> {
    List<ServiceReview> findByServiceId(UUID serviceId);
}

