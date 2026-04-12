package com.unityonline.gameserver.common.util;

import java.time.Instant;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class IdGenerator {

    private final StringRedisTemplate stringRedisTemplate;

    public IdGenerator(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 生成账号 ID，优先使用 Redis 自增保证单调递增。
     */
    public long nextAccountId() {
        return nextId("game:id:account");
    }

    /**
     * 生成玩家 ID，优先使用 Redis 自增保证单调递增。
     */
    public long nextPlayerId() {
        return nextId("game:id:player");
    }

    /**
     * 根据 Redis key 生成业务主键。
     */
    public long nextId(String redisKey) {
        try {
            Long value = stringRedisTemplate.opsForValue().increment(redisKey);
            if (value != null) {
                return value;
            }
        } catch (DataAccessException ignored) {
            // Redis 不可用时使用时间兜底，避免阻塞本地开发。
        }
        return Instant.now().toEpochMilli();
    }
}
