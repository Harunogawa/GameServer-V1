package com.unityonline.gameserver.common.auth;

import com.unityonline.gameserver.common.constant.RedisKeyConstants;
import com.unityonline.gameserver.module.auth.entity.AuthAccountEntity;
import java.time.Duration;
import java.util.Optional;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuthTokenService {

    private final StringRedisTemplate stringRedisTemplate;

    public AuthTokenService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 将 token 与账号信息写入 Redis。
     */
    public void storeToken(String token, AuthAccountEntity account, long ttlSeconds) {
        String value = account.getAccountId() + "|" + account.getPlayerId() + "|" + account.getUsername();
        stringRedisTemplate.opsForValue().set(buildKey(token), value, Duration.ofSeconds(ttlSeconds));
    }

    /**
     * 根据 token 从 Redis 中读取账号信息。
     */
    public Optional<AuthTokenPayload> parseToken(String token) {
        String value = stringRedisTemplate.opsForValue().get(buildKey(token));
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        String[] parts = value.split("\\|", 3);
        if (parts.length < 3) {
            return Optional.empty();
        }
        return Optional.of(new AuthTokenPayload(Long.parseLong(parts[0]), Long.parseLong(parts[1]), parts[2], token));
    }

    /**
     * 刷新 token 的过期时间。
     */
    public void refreshToken(String token, long ttlSeconds) {
        stringRedisTemplate.expire(buildKey(token), Duration.ofSeconds(ttlSeconds));
    }

    /**
     * 删除 Redis 中的 token。
     */
    public void deleteToken(String token) {
        stringRedisTemplate.delete(buildKey(token));
    }

    /**
     * 判断 token 是否仍然存在于 Redis。
     */
    public boolean exists(String token) {
        Boolean exists = stringRedisTemplate.hasKey(buildKey(token));
        return Boolean.TRUE.equals(exists);
    }

    /**
     * 组装 Redis 中的 token key。
     */
    private String buildKey(String token) {
        return RedisKeyConstants.AUTH_TOKEN_PREFIX + token;
    }
}
