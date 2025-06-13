package com.zentu.zentu_core.service.repository;

import com.zentu.zentu_core.service.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BidRepository extends JpaRepository<Bid, UUID> {
    List<Bid> findByServiceId(UUID serviceId);
}