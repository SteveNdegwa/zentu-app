package com.zentu.zentu_core.service.entity;

import com.zentu.zentu_core.base.entity.BaseEntity;
import com.zentu.zentu_core.service.enums.ServiceType;
import com.zentu.zentu_core.base.enums.State;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "services")
@Getter
@Setter
public class Service extends BaseEntity {

    @Column(name = "name", nullable = true)
    @NotNull(message = "Service name is required")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = true)
    private ServiceType type = ServiceType.CUSTOM_SERVICE;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = true)
    private BigDecimal price;

    @Column(name = "location")
    private String location;

    @Column(name = "contact_phone", nullable = true)
    private String contactPhone;

    @Enumerated(EnumType.STRING)
    private State state = State.ACTIVE;

    @Column(name="provider", nullable = false)
    private String provider;

}

