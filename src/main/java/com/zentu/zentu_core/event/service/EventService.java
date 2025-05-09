package com.zentu.zentu_core.event.service;

import com.zentu.zentu_core.base.enums.State;
import com.zentu.zentu_core.event.dto.*;
import com.zentu.zentu_core.event.entity.Attendee;
import com.zentu.zentu_core.event.entity.Event;
import com.zentu.zentu_core.event.enums.AttendeeState;
import com.zentu.zentu_core.event.enums.EventState;
import com.zentu.zentu_core.event.repository.AttendeeRepository;
import com.zentu.zentu_core.event.repository.EventRepository;
import com.zentu.zentu_core.group.entity.Group;
import com.zentu.zentu_core.group.entity.GroupMembership;
import com.zentu.zentu_core.group.repository.GroupMembershipRepository;
import com.zentu.zentu_core.group.repository.GroupRepository;
import com.zentu.zentu_core.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final AttendeeRepository attendeeRepository;
    private final GroupRepository groupRepository;
    private final GroupMembershipRepository groupMembershipRepository;

    @Transactional
    private void initializeAttendees(Event event){
        List<GroupMembership> memberships = groupMembershipRepository.findAllByGroupAndState(
                event.getGroup(), State.ACTIVE);

        for (GroupMembership member : memberships){
            Attendee attendee = Attendee.builder().user(member.getUser()).event(event).build();
            attendeeRepository.save(attendee);
        }
    }

    @Transactional
    public UUID createEvent(CreateEventRequest request, User user){
        Group group = groupRepository.findByIdAndState(request.getGroupId(), State.ACTIVE)
                .orElseThrow(()-> new RuntimeException("Group not found"));

        if(!groupMembershipRepository.existsByUserAndGroupAndState(user, group, State.ACTIVE)){
            throw new RuntimeException("User is not a member of the group");
        }

        Event event = eventRepository.save(
                Event.builder()
                        .name(request.getName())
                        .description(request.getDescription())
                        .location(request.getLocation())
                        .startTime(request.getStartTime())
                        .endTime(request.getEndTime())
                        .creator(user)
                        .group(group)
                        .build()
        );

        initializeAttendees(event);

        return event.getId();
    }

    @Transactional
    public void updateEvent(UpdateEventRequest request, UUID eventId, User user){
        Event event = eventRepository.findById(eventId).orElseThrow(()-> new RuntimeException("Event not found"));

        if(!event.getCreator().equals(user)){
            throw new RuntimeException("Only the event creator can update the event");
        }

        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());

        eventRepository.save(event);
    }

    @Transactional
    public void deleteEvent(UUID eventId, User user){
        Event event = eventRepository.findById(eventId).orElseThrow(()-> new RuntimeException("Event not found"));

        if(!event.getCreator().equals(user)){
            throw new RuntimeException("Only the event creator can update the event");
        }

        eventRepository.delete(event);
    }

    @Transactional
    public void togglePostponeEvent(UUID eventId, User user) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getCreator().equals(user)) {
            throw new RuntimeException("Only the event creator can modify the event state");
        }

        if (event.getState() == EventState.POSTPONED) {
            event.setState(EventState.SCHEDULED);
            event.setState(determineEventState(event));
        } else {
            event.setState(EventState.POSTPONED);
        }

        eventRepository.save(event);
    }

    @Transactional
    public void toggleCancelEvent(UUID eventId, User user) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getCreator().equals(user)) {
            throw new RuntimeException("Only the event creator can modify the event state");
        }

        if (event.getState() == EventState.CANCELLED) {
            event.setState(EventState.SCHEDULED);
            event.setState(determineEventState(event));
        } else {
            event.setState(EventState.CANCELLED);
        }

        eventRepository.save(event);
    }

    @Transactional
    public void toggleArchiveEvent(UUID eventId, User user) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getCreator().equals(user)) {
            throw new RuntimeException("Only the event creator can modify the event state");
        }

        if (event.getState() == EventState.ARCHIVED) {
            event.setState(EventState.SCHEDULED);
            event.setState(determineEventState(event));
        } else {
            event.setState(EventState.ARCHIVED);
        }

        eventRepository.save(event);
    }

    private boolean isFinalAttendeeState(AttendeeState state) {
        return List.of(AttendeeState.ATTENDED, AttendeeState.NOT_ATTENDED).contains(state);
    }

    private boolean isFutureEventState(EventState state) {
        return List.of(EventState.SCHEDULED, EventState.UPCOMING).contains(state);
    }

    private boolean isPendingAttendeeState(AttendeeState state) {
        return List.of(
                AttendeeState.NOT_RESPONDED,
                AttendeeState.ATTENDING,
                AttendeeState.NOT_ATTENDING,
                AttendeeState.MAYBE
        ).contains(state);
    }

    private boolean isPastOrOngoingEventState(EventState state) {
        return List.of(EventState.ONGOING, EventState.COMPLETED).contains(state);
    }

    public void validateAttendeeEventState(AttendeeState attendeeState, EventState computedState) {
        boolean invalidFinalStateForFutureEvent = isFinalAttendeeState(attendeeState) &&
                isFutureEventState(computedState);
        boolean invalidPendingStateForPastOrOngoingEvent = isPendingAttendeeState(attendeeState) &&
                isPastOrOngoingEventState(computedState);

        if (invalidFinalStateForFutureEvent || invalidPendingStateForPastOrOngoingEvent) {
            throw new RuntimeException(String.format(
                    "Cannot set state %s to %s event",
                    attendeeState,
                    computedState
            ));
        }
    }

    @Transactional
    public void rsvpToEvent(RsvpToEventRequest request, UUID eventId, User user){
        Event event = eventRepository.findById(eventId).orElseThrow(()-> new RuntimeException("Event not found"));

        EventState eventState = determineEventState(event);

        if (List.of(EventState.POSTPONED, EventState.ARCHIVED, EventState.CANCELLED).contains(eventState)){
            throw new RuntimeException("Cannot RSVP to this event");
        }

        AttendeeState attendeeState;
        try{
            attendeeState = AttendeeState.valueOf(request.getState().toUpperCase());
        }catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid state");
        }

        validateAttendeeEventState(attendeeState, eventState);

        Attendee attendee = attendeeRepository.findByUserAndEvent(user, event)
                .orElseThrow(()-> new RuntimeException("Attendee not found"));

        attendee.setState(attendeeState);

        attendeeRepository.save(attendee);
    }

    private EventState determineEventState(Event event) {
        if (List.of(EventState.CANCELLED, EventState.POSTPONED, EventState.ARCHIVED).contains(event.getState())) {
            return event.getState();
        }

        LocalDateTime now = LocalDateTime.now();

        if (event.getStartTime().isBefore(now) && event.getEndTime().isAfter(now)) {
            return EventState.ONGOING;
        } else if (event.getEndTime().isBefore(now)) {
            return EventState.COMPLETED;
        } else if (!event.getStartTime().isAfter(now.plusDays(5))) {
            return EventState.UPCOMING;
        } else {
            return EventState.SCHEDULED;
        }
    }

    private AttendeeDto convertToAttendeeDto(Attendee attendee){
        return AttendeeDto.builder()
                .userId(attendee.getUser().getId())
                .fullName(attendee.getUser().getFullName())
                .eventId(attendee.getEvent().getId())
                .eventName(attendee.getEvent().getName())
                .state(attendee.getState().toString())
                .build();
    }

    @Transactional(readOnly = true)
    private List<Attendee> getEventAttendees(Event event){
        return attendeeRepository.findAllByEvent(event);
    }

    @Transactional
    private EventDto convertToEventDto(Event event){
        EventState computedState = determineEventState(event);

        if(!event.getState().equals(computedState)){
            event.setState(computedState);
            eventRepository.save(event);
        }

        List<AttendeeDto> attendees = getEventAttendees(event).stream().map(this::convertToAttendeeDto).toList();
        return EventDto.builder()
                .name(event.getName())
                .description(event.getDescription())
                .location(event.getLocation())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .groupId(event.getGroup().getId())
                .groupName(event.getGroup().getName())
                .creatorId(event.getCreator().getId())
                .creatorName(event.getCreator().getFullName())
                .state(computedState.toString())
                .attendees(attendees)
                .build();
    }

    @Transactional(readOnly = true)
    public EventDto getEvent(UUID eventId){
        Event event = eventRepository.findById(eventId).orElseThrow(()-> new RuntimeException("Event not found"));
        return convertToEventDto(event);
    }

    @Transactional(readOnly = true)
    public List<EventDto> getEventsByCreator(User user){
        List<Event> events = eventRepository.findAllByCreatorOrderByCreatedAt(user);
        return events.stream().map(this::convertToEventDto).toList();
    }

    @Transactional(readOnly = true)
    public List<EventDto> getEventsByGroup(UUID groupId, User user){
        Group group = groupRepository.findByIdAndState(groupId, State.ACTIVE)
                .orElseThrow(()-> new RuntimeException("Group not found"));

        if(!groupMembershipRepository.existsByUserAndGroupAndState(user, group, State.ACTIVE)){
            throw new RuntimeException("User is not a member of the group");
        }

        List<Event> events = eventRepository.findAllByGroupOrderByCreatedAt(group);
        return events.stream().map(this::convertToEventDto).toList();
    }

    @Transactional(readOnly = true)
    public List<EventDto> getEventsByUser(User user){
        List<Group> groups = groupMembershipRepository.findGroupsByUser(user);

        List<Event> events = eventRepository.findAllByGroupInOrderByCreatedAt(groups);
        return events.stream().map(this::convertToEventDto).toList();
    }

}
