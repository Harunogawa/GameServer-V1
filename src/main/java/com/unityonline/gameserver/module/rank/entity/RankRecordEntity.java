package com.unityonline.gameserver.module.rank.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class RankRecordEntity {

    private Long id;
    private Integer rankType;
    private Long playerId;
    private Long score;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
}
