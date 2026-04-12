package com.unityonline.gameserver.module.auth.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class AuthAccountEntity {

    private Long id;
    private Long accountId;
    private Long playerId;
    private String username;
    private String passwordHash;
    private String salt;
    private LocalDateTime lastLoginTime;
    private String lastLoginIp;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
