package com.unityonline.gameserver.module.inventory.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PlayerInventoryEntity {

    private Long id;
    private Long playerId;
    private Long itemId;
    private Integer itemType;
    private Long quantity;
    private String extJson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
