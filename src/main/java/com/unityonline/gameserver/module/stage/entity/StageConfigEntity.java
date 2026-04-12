package com.unityonline.gameserver.module.stage.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class StageConfigEntity {

    private Long id;
    private Long stageId;
    private String stageName;
    private Long rewardItemId;
    private Integer rewardItemType;
    private Long rewardQuantity;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
