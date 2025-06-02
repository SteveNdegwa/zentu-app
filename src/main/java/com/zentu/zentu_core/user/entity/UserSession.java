package com.zentu.zentu_core.user.entity;
import com.zentu.zentu_core.base.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSession extends BaseEntity {

    @ManyToOne
    private AppUser user;

    private String sessionType;

    @CreationTimestamp
    private LocalDateTime timestamp;

    public UserSession(Object o, AppUser user, String logout, Object o1) {
    }

    @Override
    public String toString() {
        return user.getUserId() + " - " + sessionType + " at " + timestamp;
    }
}
