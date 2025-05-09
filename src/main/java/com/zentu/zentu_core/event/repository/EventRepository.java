package com.zentu.zentu_core.event.repository;

import com.zentu.zentu_core.event.entity.Event;
import com.zentu.zentu_core.event.enums.EventState;
import com.zentu.zentu_core.group.entity.Group;
import com.zentu.zentu_core.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findAllByGroupOrderByCreatedAt(Group group);

    List<Event> findAllByGroupInOrderByCreatedAt(List<Group> groups);

    List<Event> findAllByCreatorOrderByCreatedAt(User creator);

    List<Event> findAllByStateOrderByCreatedAt(EventState state);

    @Query("SELECT e FROM Event e WHERE e.startTime BETWEEN :startOfDay AND :endOfDay")
    List<Event> findEventsForDay(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );


}
