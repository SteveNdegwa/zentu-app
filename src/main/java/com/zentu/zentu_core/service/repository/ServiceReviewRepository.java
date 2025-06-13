package com.zentu.zentu_core.service.repository;

import com.zentu.zentu_core.service.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceReviewRepository extends JpaRepository<ServiceReview, Long> {
    List<ServiceReview> findByServiceId(Long serviceId);
}

