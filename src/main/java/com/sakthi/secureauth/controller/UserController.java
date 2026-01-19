package com.sakthi.secureauth.controller;

import com.sakthi.secureauth.dto.request.UpdateUserRequest;
import com.sakthi.secureauth.dto.response.UserResponse;
import com.sakthi.secureauth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            org.springframework.security.core.Authentication authentication
    ) {
        String email = authentication.getName(); // JWT subject
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(userService.getAllUsers(page, size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }
}
