package com.sakthi.secureauth.dto.response;

import com.sakthi.secureauth.model.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class UserResponse {
    private UUID id;
    private String fullName;
    private String email;
    private Role role;
    private Instant createdAt;
}
