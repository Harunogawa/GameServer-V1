package com.unityonline.gameserver.module.quest.dto;

public record QuestDto(
        Long questId,
        String questName,
        String questDesc,
        Integer status,
        Integer progress,
        Integer targetValue,
        boolean rewardClaimed
) {
}
