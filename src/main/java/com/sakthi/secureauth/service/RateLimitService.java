package com.sakthi.secureauth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final StringRedisTemplate redisTemplate;

    private static final int MAX_REQUESTS = 100;
    private static final Duration WINDOW = Duration.ofMinutes(1);

    /**
     * @param key unique identifier (IP address or email)
     * @return true if request is allowed, false if rate limited
     */
    public boolean isAllowed(String key) {

        String redisKey = "rate_limit:" + key;

        Long currentCount = redisTemplate.opsForValue().increment(redisKey);

        if (currentCount != null && currentCount == 1) {
            redisTemplate.expire(redisKey, WINDOW);
        }

        return currentCount != null && currentCount <= MAX_REQUESTS;
    }
}
