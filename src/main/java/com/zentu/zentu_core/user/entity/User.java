package com.zentu.zentu_core.user.entity;

import com.zentu.zentu_core.base.entity.BaseEntity;
import com.zentu.zentu_core.base.enums.State;
import com.zentu.zentu_core.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {
    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String otherName;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // hashed

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    private Boolean isSuperUser = false;

    private Boolean isVerified = false;

    private String deviceId;

    @Enumerated(EnumType.STRING)
    private State state = State.ACTIVE;

    public String getFullName() {
        return java.util.stream.Stream.of(firstName, otherName, lastName)
                .filter(name -> name != null && !name.isBlank())
                .collect(java.util.stream.Collectors.joining(" "));
    }

}
