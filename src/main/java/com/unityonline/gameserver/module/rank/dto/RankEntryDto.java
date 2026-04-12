package com.unityonline.gameserver.module.rank.dto;

public record RankEntryDto(
        Integer rankNo,
        Long playerId,
        String nickname,
        Long score
) {
}
