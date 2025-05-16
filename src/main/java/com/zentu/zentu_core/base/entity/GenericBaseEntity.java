package com.zentu.zentu_core.base.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;


@MappedSuperclass
@Getter
@Setter
public class GenericBaseEntity extends BaseEntity{
    @Column(nullable = false)
    private String name;

    @Column(length = 255)
    private String description;
}
