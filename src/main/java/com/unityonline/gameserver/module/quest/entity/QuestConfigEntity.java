package com.unityonline.gameserver.module.quest.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class QuestConfigEntity {

    private Long id;
    private Long questId;
    private String questName;
    private String questDesc;
    private Integer questType;
    private Integer targetValue;
    private Long rewardItemId;
    private Integer rewardItemType;
    private Long rewardQuantity;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
