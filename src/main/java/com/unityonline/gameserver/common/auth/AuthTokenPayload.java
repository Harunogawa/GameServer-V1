package com.unityonline.gameserver.common.auth;

public record AuthTokenPayload(
        Long accountId,
        Long playerId,
        String username,
        String token
) {
}
