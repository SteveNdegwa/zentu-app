package com.zentu.zentu_core.event.entity;

import com.zentu.zentu_core.event.enums.AttendeeState;
import com.zentu.zentu_core.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "attendees")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Attendee {
    @ManyToOne(optional = false)
    @JoinColumn(name = "attendee_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Event event;

    @Enumerated(EnumType.STRING)
    private AttendeeState state = AttendeeState.NOT_RESPONDED;
}
