package com.unityonline.gameserver.module.quest.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PlayerQuestEntity {

    private Long id;
    private Long playerId;
    private Long questId;
    private Integer questStatus;
    private Integer progress;
    private Integer rewardClaimed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
