package com.sakthi.secureauth.service;

import com.sakthi.secureauth.dto.request.UpdateUserRequest;
import com.sakthi.secureauth.dto.response.UserResponse;
import com.sakthi.secureauth.exception.ResourceNotFoundException;
import com.sakthi.secureauth.model.User;
import com.sakthi.secureauth.repository.UserRepository;
import com.sakthi.secureauth.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Cacheable(value = "users", key = "#userId")
    public UserResponse getUserById(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        return mapToResponse(user);
    }

    @Cacheable(value = "users_page", key = "#page + '-' + #size")
    public Page<UserResponse> getAllUsers(int page, int size) {

        Pageable pageable = PaginationUtil.createPageRequest(page, size);

        Page<User> usersPage = userRepository.findAll(pageable);

        return usersPage.map(this::mapToResponse);
    }
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#userId"),
            @CacheEvict(value = "users_page", allEntries = true)
    })
    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        user.setFullName(request.getFullName());

        User updated = userRepository.save(user);

        return mapToResponse(updated);
    }


    public UserResponse getUserByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        return mapToResponse(user);
    }


    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
