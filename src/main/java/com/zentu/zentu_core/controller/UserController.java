package com.zentu.zentu_core.controller;

import com.zentu.zentu_core.dto.user.CreateUserRequest;
import com.zentu.zentu_core.dto.user.UpdateUserRequest;
import com.zentu.zentu_core.dto.user.UpdateUserRoleRequest;
import com.zentu.zentu_core.dto.user.UserDto;
import com.zentu.zentu_core.enums.AdministrativeRole;
import com.zentu.zentu_core.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UUID> createUser(@RequestBody @Valid CreateUserRequest request) {
        return ResponseEntity.ok(userService.createUser(request, AdministrativeRole.USER, Boolean.FALSE));
    }

    @PostMapping("/admins")
    public ResponseEntity<UUID> createAdmin(@RequestBody @Valid CreateUserRequest request) {
        return ResponseEntity.ok(userService.createUser(request, AdministrativeRole.ADMIN, Boolean.FALSE));
    }

    @PostMapping("/superusers")
    public ResponseEntity<UUID> createSuperUsers(@RequestBody @Valid CreateUserRequest request) {
        return ResponseEntity.ok(userService.createUser(request, AdministrativeRole.ADMIN, Boolean.TRUE));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable UUID userId, @RequestBody @Valid UpdateUserRequest request) {
        userService.updateUser(request, userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<Void> updateUserRole(
            @PathVariable UUID userId, @RequestBody @Valid UpdateUserRoleRequest request) {
        userService.updateUserRole(userId, request.getRole());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/superuser")
    public ResponseEntity<Void> updateUserIsSuperUser(@PathVariable UUID userId) {
        userService.updateUserIsSuperUser(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

}
