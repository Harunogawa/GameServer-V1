package com.unityonline.gameserver.module.save.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PlayerSaveEntity {

    private Long id;
    private Long playerId;
    private Integer saveSlot;
    private String saveVersion;
    private String saveDataJson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
