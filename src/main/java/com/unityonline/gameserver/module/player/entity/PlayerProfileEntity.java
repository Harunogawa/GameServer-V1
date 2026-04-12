package com.unityonline.gameserver.module.player.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PlayerProfileEntity {

    private Long id;
    private Long playerId;
    private Long accountId;
    private String avatarUrl;
    private LocalDateTime lastLoginTime;
    private LocalDateTime lastLogoutTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
