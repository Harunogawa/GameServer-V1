package com.unityonline.gameserver.module.player.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PlayerEntity {

    private Long id;
    private Long playerId;
    private Long accountId;
    private String nickname;
    private Integer level;
    private Long exp;
    private Long gold;
    private Long diamond;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
