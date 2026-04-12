package com.unityonline.gameserver.module.auth.dto;

public record LoginCommand(
        String username,
        String password,
        String clientIp
) {
}
