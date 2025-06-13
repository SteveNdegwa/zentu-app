package com.zentu.zentu_core.service.entity;

import com.zentu.zentu_core.base.entity.BaseEntity;
import com.zentu.zentu_core.base.enums.State;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "service_reviews")
@Getter
@Setter
public class ServiceReview extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service", nullable = false)
    private Service service;

    @Column(name="reviewer", nullable = false)
    private String reviewer;

    @Min(1)
    @Max(5)
    @Column(name = "rating", nullable = false)
    private int rating;

    @Size(max = 1000)
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Enumerated(EnumType.STRING)
    private State state = State.ACTIVE;

    @Column(name = "visible", nullable = false)
    private boolean visible = true;
}
