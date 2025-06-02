
package com.zentu.zentu_core.user.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zentu.zentu_core.base.entity.BaseEntity;
import com.zentu.zentu_core.common.utils.JsonConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppUser extends BaseEntity {

    @Column(unique = true)
    private String userId;

    @Column(columnDefinition = "json")
    @Convert(converter = JsonConverter.class)
    private Map<String, Object> metadata;

    private Boolean isActive = true;

    private LocalDateTime lastLogin;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private java.util.List<UserSession> sessions;

    @Override
    public String toString() {
        return userId;
    }
}
