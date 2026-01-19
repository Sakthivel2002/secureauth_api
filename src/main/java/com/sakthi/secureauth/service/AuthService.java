package com.sakthi.secureauth.service;

import com.sakthi.secureauth.dto.request.LoginRequest;
import com.sakthi.secureauth.dto.request.RefreshTokenRequest;
import com.sakthi.secureauth.dto.request.RegisterRequest;
import com.sakthi.secureauth.dto.response.AuthResponse;
import com.sakthi.secureauth.exception.InvalidRefreshTokenException;
import com.sakthi.secureauth.exception.ResourceNotFoundException;
import com.sakthi.secureauth.model.RefreshToken;
import com.sakthi.secureauth.model.User;
import com.sakthi.secureauth.repository.RefreshTokenRepository;
import com.sakthi.secureauth.repository.UserRepository;
import com.sakthi.secureauth.util.HashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email already registered");

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        var userDetails = (org.springframework.security.core.userdetails.User) auth.getPrincipal();

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String accessToken = jwtService.generateAccessToken(user.getEmail(), user.getRole());
        RefreshToken refreshToken = createRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String hashedToken = HashUtil.sha256(request.getRefreshToken());

        RefreshToken storedToken = refreshTokenRepository.findByToken(hashedToken)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

        User user = storedToken.getUser();

        if (storedToken.isRevoked()) {
            revokeAllTokens(user);
            throw new InvalidRefreshTokenException("Refresh token reuse detected. All sessions revoked.");
        }

        if (storedToken.getExpiryDate().isBefore(Instant.now())) {
            revokeAllTokens(user);
            throw new InvalidRefreshTokenException("Refresh token expired");
        }

        // Create new refresh token before revoking old
        RefreshToken newRefreshToken = createRefreshToken(user);

        // Generate access token
        String accessToken = jwtService.generateAccessToken(user.getEmail(), user.getRole());

        // Revoke old token safely
        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        // Limit active tokens
        limitActiveTokens(user, 5);

        return new AuthResponse(accessToken, newRefreshToken.getToken());
    }

    public void logout(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    private RefreshToken createRefreshToken(User user) {
        String rawToken = UUID.randomUUID().toString();
        String hashedToken = HashUtil.sha256(rawToken);

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(hashedToken);
        token.setExpiryDate(Instant.now().plusMillis(refreshTokenExpiration));

        refreshTokenRepository.save(token);

        token.setToken(rawToken); // return raw token to client
        return token;
    }

    private void revokeAllTokens(User user) {
        refreshTokenRepository.findByUser(user).forEach(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

    private void limitActiveTokens(User user, int maxTokens) {
        List<RefreshToken> tokens = refreshTokenRepository.findByUser(user);
        if (tokens.size() > maxTokens) {
            tokens.stream()
                    .sorted(Comparator.comparing(RefreshToken::getExpiryDate))
                    .limit(tokens.size() - maxTokens)
                    .forEach(refreshTokenRepository::delete);
        }
    }
}
