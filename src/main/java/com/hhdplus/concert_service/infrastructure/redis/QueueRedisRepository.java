package com.hhdplus.concert_service.infrastructure.redis;

import com.hhdplus.concert_service.infrastructure.entity.Queue;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class QueueRedisRepository {
    private final RedisTemplate<String, String> redisTemplate;

    private static final String QUEUE = "queue";
    private static final String ACTIVE_TOKEN = "active";
    private static final int MAX_QUEUE_SIZE = 100;
    private static final int MAX_ACTIVE_MINUTES = 5;

    // 대기열에 추가
    public void addQueue(String token) {
        redisTemplate.opsForZSet().add(QUEUE, token, System.currentTimeMillis());
    }

    // 대기열 순서 조회
    public Long getQueueOrder(String token) {
        return redisTemplate.opsForZSet().rank(QUEUE, token);
    }

    // 토큰 검증
    public Boolean isTokenActive(String token) {
        return redisTemplate.opsForSet().isMember(ACTIVE_TOKEN, token);
    }

    // 토큰 활성화
    public void activateTokens() {
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        SetOperations<String, String> setOps = redisTemplate.opsForSet();

        int currentActiveCount = getCurrentActiveTokenCount(setOps);
        int tokensToActivate = MAX_QUEUE_SIZE - currentActiveCount;

        Set<String> tokens = zSetOps.range(QUEUE, 0, tokensToActivate);
        if (tokens == null || tokens.isEmpty()) {
            return;
        }

        activateAndRemoveTokens(zSetOps, setOps, tokens);
    }

    private int getCurrentActiveTokenCount(SetOperations<String, String> setOps) {
        Set<String> activeTokens = setOps.members(ACTIVE_TOKEN);
        return activeTokens == null ? 0 : activeTokens.size();
    }

    private void activateAndRemoveTokens(ZSetOperations<String, String> zSetOps, SetOperations<String, String> setOps, Set<String> tokens) {
        redisTemplate.executePipelined((RedisCallback<Void>) conn -> {
            conn.openPipeline();

            zSetOps.remove(QUEUE, tokens.toArray());
            tokens.forEach(token -> setOps.add(ACTIVE_TOKEN, formatTokenWithTimestamp(token)));

            conn.closePipeline();
            return null;
        });
    }

    private String formatTokenWithTimestamp(String token) {
        return String.format("%s:%d", token, System.currentTimeMillis());
    }

    // 토큰 만료
    public void expireToken(String token) {
        redisTemplate.opsForSet().remove(ACTIVE_TOKEN, token);
    }

    // 토큰 만료 처리
    public void expireTokens() {
        SetOperations<String, String> setOps = redisTemplate.opsForSet();
        Set<String> activeTokens = setOps.members(ACTIVE_TOKEN);
        if (activeTokens == null || activeTokens.isEmpty()) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        activeTokens.forEach(tokenWithTime -> {
            long tokenTime = extractTimestampFromToken(tokenWithTime);
            if (isTokenExpired(currentTime, tokenTime)) {
                setOps.remove(ACTIVE_TOKEN, tokenWithTime);
            }
        });
    }

    private long extractTimestampFromToken(String tokenWithTime) {
        String[] parts = tokenWithTime.split(":");

        try {
            return Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid token format: " + tokenWithTime, e);
        }

    }

    private boolean isTokenExpired(long currentTime, long tokenTime) {
        return currentTime - tokenTime > MAX_ACTIVE_MINUTES * 60 * 1000;
    }
}
