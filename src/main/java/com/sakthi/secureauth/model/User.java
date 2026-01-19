package com.sakthi.secureauth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email")
        },
        indexes = {
                @Index(name = "idx_user_email", columnList = "email")
        }
)
@Getter
@Setter
public class User extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String fullName;

    @Email
    @NotBlank
    @Column(nullable = false)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false)
    private boolean locked = false;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RefreshToken> refreshTokens;
}
