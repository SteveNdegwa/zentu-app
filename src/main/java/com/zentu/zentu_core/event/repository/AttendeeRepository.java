package com.zentu.zentu_core.event.repository;

import com.zentu.zentu_core.event.entity.Attendee;
import com.zentu.zentu_core.event.entity.Event;
import com.zentu.zentu_core.event.enums.AttendeeState;
import com.zentu.zentu_core.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendeeRepository extends JpaRepository<Attendee, UUID> {
    List<Attendee> findAllByUser(User user);

    List<Attendee> findAllByUserAndState(User user, AttendeeState state);

    Optional<Attendee> findByUserAndEvent(User user, Event event);

    List<Attendee> findAllByEvent(Event event);

    List<Attendee> findAllByEventAndState(Event event, AttendeeState state);
}
