package com.unityonline.gameserver.module.save.dto;

public record SaveGameDto(
        Long playerId,
        Integer slot,
        String saveDataJson,
        String saveVersion
) {
}
