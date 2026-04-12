package com.unityonline.gameserver.common.context;

public record LoginUser(
        Long accountId,
        Long playerId,
        String username,
        String token
) {
}
