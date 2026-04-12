package com.unityonline.gameserver.module.stage.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PlayerStageEntity {

    private Long id;
    private Long playerId;
    private Long stageId;
    private Integer starCount;
    private Integer clearStatus;
    private Long bestScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
