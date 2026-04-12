package com.unityonline.gameserver.module.player.controller;

import com.unityonline.gameserver.common.context.LoginPlayerContext;
import com.unityonline.gameserver.common.exception.BizException;
import com.unityonline.gameserver.common.proto.ProtoResponseBuilder;
import com.unityonline.gameserver.module.player.dto.PlayerProfileDto;
import com.unityonline.gameserver.module.player.service.PlayerService;
import com.unityonline.gameserver.proto.game.player.InitPlayerRequest;
import com.unityonline.gameserver.proto.game.player.InitPlayerResponse;
import com.unityonline.gameserver.proto.game.player.PlayerProfileRequest;
import com.unityonline.gameserver.proto.game.player.PlayerProfileResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

    private static final String PROTOBUF_VALUE = "application/x-protobuf";

    private final PlayerService playerService;
    private final ProtoResponseBuilder protoResponseBuilder;

    public PlayerController(PlayerService playerService, ProtoResponseBuilder protoResponseBuilder) {
        this.playerService = playerService;
        this.protoResponseBuilder = protoResponseBuilder;
    }

    /**
     * 初始化玩家数据，通常用于手动补建玩家。
     */
    @PostMapping(value = "/init", consumes = PROTOBUF_VALUE, produces = PROTOBUF_VALUE)
    public InitPlayerResponse initPlayer(@RequestBody InitPlayerRequest request) {
        long playerId = playerService.initializePlayer(request.getAccountId(), request.getNickname());
        return InitPlayerResponse.newBuilder()
                .setResult(protoResponseBuilder.success())
                .setPlayerId(playerId)
                .build();
    }

    /**
     * 获取玩家基础资料。
     */
    @PostMapping(value = "/profile", consumes = PROTOBUF_VALUE, produces = PROTOBUF_VALUE)
    public PlayerProfileResponse getProfile(@RequestBody PlayerProfileRequest request) {
        Long loginPlayerId = LoginPlayerContext.getPlayerId();
        if (loginPlayerId == null) {
            throw new BizException(4011, "login required");
        }
        if (request.getPlayerId() != 0 && !loginPlayerId.equals(request.getPlayerId())) {
            throw new BizException(4012, "cannot query other player");
        }
        PlayerProfileDto profile = playerService.getPlayerProfile(loginPlayerId);
        return PlayerProfileResponse.newBuilder()
                .setResult(protoResponseBuilder.success())
                .setAccountId(profile.accountId())
                .setPlayerId(profile.playerId())
                .setUsername(profile.username())
                .setNickname(profile.nickname())
                .setLevel(profile.level())
                .setExp(profile.exp())
                .setGold(profile.gold())
                .setDiamond(profile.diamond())
                .setAvatarUrl(profile.avatarUrl() == null ? "" : profile.avatarUrl())
                .build();
    }
}
