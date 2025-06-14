package com.zentu.zentu_core.service.controller;

import com.zentu.zentu_core.service.dto.*;
import com.zentu.zentu_core.service.entity.*;
import com.zentu.zentu_core.service.enums.ServiceType;
import com.zentu.zentu_core.service.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/service")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceRepository serviceRepository;
    private final BidRepository bidRepository;
    private final ServiceReviewRepository reviewRepository;

    @PostMapping("/create")
    public ResponseEntity<Service> createService(@Valid @RequestBody CreateServiceDto dto) {
        Service service = new Service();
        service.setName(dto.getName());
        service.setDescription(dto.getDescription());
        service.setPrice(dto.getPrice());
        service.setLocation(dto.getLocation());
        service.setProvider(dto.getProvider());
        service.setType(dto.getServiceType());
        return ResponseEntity.ok(serviceRepository.save(service));
    }

    @GetMapping
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    @PostMapping("/{serviceId}/bids")
    public ResponseEntity<Bid> placeBid(@PathVariable UUID serviceId, @Valid @RequestBody CreateBidDto dto) {
        Service service = serviceRepository.findById(serviceId).orElseThrow();
        Bid bid = new Bid();
        bid.setService(service);
        bid.setBidAmount(dto.getBidAmount());
        bid.setMessage(dto.getMessage());
        bid.setPreferredDate(dto.getPreferredDate());
        bid.setBidder(dto.getBidder());
        return ResponseEntity.ok(bidRepository.save(bid));
    }

    @GetMapping("/{serviceId}/bids")
    public List<Bid> getBidsForService(@PathVariable UUID serviceId) {
        return bidRepository.findByServiceId(serviceId);
    }

    @PostMapping("/{serviceId}/reviews")
    public ResponseEntity<ServiceReview> addReview(@PathVariable UUID serviceId, @Valid @RequestBody CreateReviewDto dto) {
        Service service = serviceRepository.findById(serviceId).orElseThrow();
        ServiceReview review = new ServiceReview();
        review.setService(service);
        review.setReviewer(dto.getReviewer());
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        return ResponseEntity.ok(reviewRepository.save(review));
    }

    @GetMapping("/{serviceId}/reviews")
    public List<ServiceReview> getReviewsForService(@PathVariable UUID serviceId) {
        return reviewRepository.findByServiceId(serviceId);
    }

    @GetMapping("/types")
    public ResponseEntity<List<Map<String, String>>> getServiceTypes() {
        List<Map<String, String>> types = Arrays.stream(ServiceType.values())
                .map(type -> Map.of(
                        "key", type.name(),
                        "label", type.getDisplayName()
                ))
                .toList();
        return ResponseEntity.ok(types);
    }

}
