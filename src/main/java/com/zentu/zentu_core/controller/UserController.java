package com.zentu.zentu_core.controller;

import com.zentu.zentu_core.dto.api.ApiResponse;
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
    public ResponseEntity<ApiResponse<UUID>> createUser(@RequestBody @Valid CreateUserRequest request) {
        UUID userId = userService.createUser(request, AdministrativeRole.USER, Boolean.FALSE);
        return ResponseEntity.ok(ApiResponse.success("User created successfully", userId));
    }

    @PostMapping("/admins")
    public ResponseEntity<ApiResponse<UUID>> createAdmin(@RequestBody @Valid CreateUserRequest request) {
        UUID userId = userService.createUser(request, AdministrativeRole.ADMIN, Boolean.FALSE);
        return ResponseEntity.ok(ApiResponse.success("Admin created successfully", userId));
    }

    @PostMapping("/superusers")
    public ResponseEntity<ApiResponse<UUID>> createSuperUsers(@RequestBody @Valid CreateUserRequest request) {
        UUID userId = userService.createUser(request, AdministrativeRole.ADMIN, Boolean.TRUE);
        return ResponseEntity.ok(ApiResponse.success("Superuser created successfully", userId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateUser(
            @PathVariable UUID userId, @RequestBody @Valid UpdateUserRequest request) {
        userService.updateUser(request, userId);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", null));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<ApiResponse<Void>> updateUserRole(
            @PathVariable UUID userId, @RequestBody @Valid UpdateUserRoleRequest request) {
        userService.updateUserRole(userId, request.getRole());
        return ResponseEntity.ok(ApiResponse.success("User's role updated successfully", null));
    }

    @PatchMapping("/{id}/superuser")
    public ResponseEntity<ApiResponse<Void>> updateUserIsSuperUser(@PathVariable UUID userId) {
        userService.updateUserIsSuperUser(userId);
        return ResponseEntity.ok(
                ApiResponse.success("User's superuser status updated successfully", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUser(@PathVariable UUID userId) {
        UserDto userDto = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success("User fetched successfully", userDto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Users fetched successfully", users));
    }

}
