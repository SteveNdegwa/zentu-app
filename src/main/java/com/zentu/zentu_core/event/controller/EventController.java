package com.zentu.zentu_core.event.controller;

import com.zentu.zentu_core.base.dto.ApiResponse;
import com.zentu.zentu_core.event.dto.CreateEventRequest;
import com.zentu.zentu_core.event.dto.EventDto;
import com.zentu.zentu_core.event.dto.RsvpToEventRequest;
import com.zentu.zentu_core.event.dto.UpdateEventRequest;
import com.zentu.zentu_core.event.service.EventService;
import com.zentu.zentu_core.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<ApiResponse<UUID>> createEvent(
            @RequestBody @Valid CreateEventRequest request,
            @AuthenticationPrincipal User user
    ) {
        UUID eventId = eventService.createEvent(request, user);
        return ResponseEntity.ok(ApiResponse.success("Event created successfully", eventId));
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<ApiResponse<Void>> updateEvent(
            @PathVariable UUID eventId,
            @RequestBody @Valid UpdateEventRequest request,
            @AuthenticationPrincipal User user
    ) {
        eventService.updateEvent(request, eventId, user);
        return ResponseEntity.ok(ApiResponse.success("Event updated successfully", null));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(
            @PathVariable UUID eventId,
            @AuthenticationPrincipal User user
    ) {
        eventService.deleteEvent(eventId, user);
        return ResponseEntity.ok(ApiResponse.success("Event deleted successfully", null));
    }

    @PostMapping("/{eventId}/rsvp")
    public ResponseEntity<ApiResponse<Void>> rsvpToEvent(
            @PathVariable UUID eventId,
            @RequestBody @Valid RsvpToEventRequest request,
            @AuthenticationPrincipal User user
    ) {
        eventService.rsvpToEvent(request, eventId, user);
        return ResponseEntity.ok(ApiResponse.success("RSVP successful", null));
    }

    @PatchMapping("/{eventId}/postpone")
    public ResponseEntity<ApiResponse<Void>> togglePostponeEvent(
            @PathVariable UUID eventId,
            @AuthenticationPrincipal User user
    ) {
        eventService.togglePostponeEvent(eventId, user);
        return ResponseEntity.ok(ApiResponse.success("Event postpone toggled successfully", null));
    }

    @PatchMapping("/{eventId}/cancel")
    public ResponseEntity<ApiResponse<Void>> toggleCancelEvent(
            @PathVariable UUID eventId,
            @AuthenticationPrincipal User user
    ) {
        eventService.toggleCancelEvent(eventId, user);
        return ResponseEntity.ok(ApiResponse.success("Event cancel toggled successfully", null));
    }

    @PatchMapping("/{eventId}/archive")
    public ResponseEntity<ApiResponse<Void>> toggleArchiveEvent(
            @PathVariable UUID eventId,
            @AuthenticationPrincipal User user
    ) {
        eventService.toggleArchiveEvent(eventId, user);
        return ResponseEntity.ok(ApiResponse.success("Event archive toggled successfully", null));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<ApiResponse<EventDto>> getEvent(
            @PathVariable UUID eventId
    ) {
        EventDto event = eventService.getEvent(eventId);
        return ResponseEntity.ok(ApiResponse.success("Event fetched successfully", event));
    }

    @GetMapping("/creator")
    public ResponseEntity<ApiResponse<List<EventDto>>> getEventsByCreator(
            @AuthenticationPrincipal User user
    ) {
        List<EventDto> events = eventService.getEventsByCreator(user);
        return ResponseEntity.ok(ApiResponse.success("Events by creator fetched successfully", events));
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<ApiResponse<List<EventDto>>> getEventsByGroup(
            @PathVariable UUID groupId,
            @AuthenticationPrincipal User user
    ) {
        List<EventDto> events = eventService.getEventsByGroup(groupId, user);
        return ResponseEntity.ok(ApiResponse.success("Events by group fetched successfully", events));
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<EventDto>>> getEventsByUser(
            @AuthenticationPrincipal User user
    ) {
        List<EventDto> events = eventService.getEventsByUser(user);
        return ResponseEntity.ok(ApiResponse.success("Events for user fetched successfully", events));
    }
}
