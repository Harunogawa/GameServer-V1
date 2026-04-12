package com.unityonline.gameserver.module.stage.dto;

public record StageResultDto(
        Long stageId,
        String stageName,
        Integer starCount,
        boolean cleared,
        Long bestScore,
        boolean firstClearReward
) {
}
