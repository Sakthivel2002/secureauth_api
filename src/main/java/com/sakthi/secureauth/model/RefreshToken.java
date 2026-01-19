package com.sakthi.secureauth.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_token_user", columnList = "user_id"),
                @Index(name = "idx_refresh_token_token", columnList = "token")
        }
)
@Getter
@Setter
public class RefreshToken extends BaseEntity {

    @Column(nullable = false, unique = true, length = 128)
    private String token; // store hashed token

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant expiryDate;

    @Column(nullable = false)
    private boolean revoked = false;

    // Optional: Track previous token if needed for rotation detection
    // @Column(length = 128)
    // private String previousToken;
}
