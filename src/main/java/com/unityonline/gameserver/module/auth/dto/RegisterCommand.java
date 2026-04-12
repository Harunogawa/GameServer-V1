package com.unityonline.gameserver.module.auth.dto;

public record RegisterCommand(
        String username,
        String password,
        String nickname,
        String clientIp
) {
}
