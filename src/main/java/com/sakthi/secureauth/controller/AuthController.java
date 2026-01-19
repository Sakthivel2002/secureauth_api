package com.sakthi.secureauth.controller;

import com.sakthi.secureauth.dto.request.LoginRequest;
import com.sakthi.secureauth.dto.request.RefreshTokenRequest;
import com.sakthi.secureauth.dto.request.RegisterRequest;
import com.sakthi.secureauth.dto.response.AuthResponse;
import com.sakthi.secureauth.model.User;
import com.sakthi.secureauth.repository.UserRepository;
import com.sakthi.secureauth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails != null) {
            User user = userRepository
                    .findByEmail(userDetails.getUsername())
                    .orElseThrow();
            authService.logout(user);
        }
        return ResponseEntity.noContent().build();
    }
}
