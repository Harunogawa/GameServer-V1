package com.unityonline.gameserver.module.auth.dto;

public record AuthResultDto(
        Long accountId,
        Long playerId,
        String token
) {
}
