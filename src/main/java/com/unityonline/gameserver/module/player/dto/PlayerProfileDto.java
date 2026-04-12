package com.unityonline.gameserver.module.player.dto;

public record PlayerProfileDto(
        Long accountId,
        Long playerId,
        String username,
        String nickname,
        Integer level,
        Long exp,
        Long gold,
        Long diamond,
        String avatarUrl
) {
}
