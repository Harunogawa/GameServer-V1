package com.unityonline.gameserver.common.util;

import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TokenUtils {

    /**
     * 生成随机业务 token。
     */
    public String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 从请求头中提取裸 token。
     */
    public String extractRawToken(String tokenValue, String prefix) {
        if (!StringUtils.hasText(tokenValue)) {
            return null;
        }
        String trimmed = tokenValue.trim();
        if (StringUtils.hasText(prefix) && trimmed.regionMatches(true, 0, prefix, 0, prefix.length())) {
            String remaining = trimmed.substring(prefix.length()).trim();
            return remaining.isEmpty() ? null : remaining;
        }
        return trimmed;
    }
}
