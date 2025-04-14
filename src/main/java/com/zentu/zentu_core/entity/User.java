package com.zentu.zentu_core.entity;

import com.zentu.zentu_core.enums.AdministrativeRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Filter(name = "stateFilter", condition = "state = :state")
public class User extends BaseEntity {
    private String firstName;

    private String lastName;

    private String otherName;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    @Column(unique = true, nullable = false)
    private String email;

    private String password; // hashed

    @Enumerated(EnumType.STRING)
    private AdministrativeRole role = AdministrativeRole.USER;

    private Boolean isSuperUser = false;

}
