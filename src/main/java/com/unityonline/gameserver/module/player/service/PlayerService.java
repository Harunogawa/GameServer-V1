package com.unityonline.gameserver.module.player.service;

import com.unityonline.gameserver.common.exception.BizException;
import com.unityonline.gameserver.common.util.IdGenerator;
import com.unityonline.gameserver.module.auth.entity.AuthAccountEntity;
import com.unityonline.gameserver.module.auth.mapper.AuthAccountMapper;
import com.unityonline.gameserver.module.player.dto.PlayerProfileDto;
import com.unityonline.gameserver.module.player.entity.PlayerEntity;
import com.unityonline.gameserver.module.player.entity.PlayerProfileEntity;
import com.unityonline.gameserver.module.player.mapper.PlayerMapper;
import com.unityonline.gameserver.module.player.mapper.PlayerProfileMapper;
import com.unityonline.gameserver.module.quest.service.QuestService;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class PlayerService {

    private final IdGenerator idGenerator;
    private final PlayerMapper playerMapper;
    private final PlayerProfileMapper playerProfileMapper;
    private final AuthAccountMapper authAccountMapper;
    private final QuestService questService;

    public PlayerService(IdGenerator idGenerator,
                         PlayerMapper playerMapper,
                         PlayerProfileMapper playerProfileMapper,
                         AuthAccountMapper authAccountMapper,
                         QuestService questService) {
        this.idGenerator = idGenerator;
        this.playerMapper = playerMapper;
        this.playerProfileMapper = playerProfileMapper;
        this.authAccountMapper = authAccountMapper;
        this.questService = questService;
    }

    /**
     * 初始化玩家数据，通常在注册时调用。
     */
    @Transactional
    public long initializePlayer(Long accountId, String nickname) {
        if (!StringUtils.hasText(nickname)) {
            throw new BizException(4001, "nickname cannot be empty");
        }
        playerMapper.findByAccountId(accountId).ifPresent(player -> {
            throw new BizException(4002, "player already initialized");
        });

        long playerId = idGenerator.nextPlayerId();
        PlayerEntity playerEntity = new PlayerEntity();
        playerEntity.setPlayerId(playerId);
        playerEntity.setAccountId(accountId);
        playerEntity.setNickname(nickname.trim());
        playerEntity.setLevel(1);
        playerEntity.setExp(0L);
        playerEntity.setGold(0L);
        playerEntity.setDiamond(0L);
        playerEntity.setStatus(1);
        playerMapper.insert(playerEntity);

        PlayerProfileEntity profileEntity = new PlayerProfileEntity();
        profileEntity.setPlayerId(playerId);
        profileEntity.setAccountId(accountId);
        profileEntity.setAvatarUrl("");
        profileEntity.setLastLoginTime(null);
        profileEntity.setLastLogoutTime(null);
        playerProfileMapper.insert(profileEntity);
        authAccountMapper.updatePlayerId(accountId, playerId);
        questService.initializePlayerQuests(playerId);
        return playerId;
    }

    /**
     * 获取玩家基础信息并拼装为 DTO。
     */
    public PlayerProfileDto getPlayerProfile(Long playerId) {
        PlayerEntity player = playerMapper.findByPlayerId(playerId)
                .orElseThrow(() -> new BizException(4041, "player not found"));
        AuthAccountEntity account = authAccountMapper.findByAccountId(player.getAccountId())
                .orElseThrow(() -> new BizException(4042, "account not found"));
        PlayerProfileEntity profile = playerProfileMapper.findByPlayerId(playerId)
                .orElseGet(() -> {
                    PlayerProfileEntity defaultProfile = new PlayerProfileEntity();
                    defaultProfile.setAvatarUrl("");
                    return defaultProfile;
                });
        return new PlayerProfileDto(
                account.getAccountId(),
                player.getPlayerId(),
                account.getUsername(),
                player.getNickname(),
                player.getLevel(),
                player.getExp(),
                player.getGold(),
                player.getDiamond(),
                profile.getAvatarUrl()
        );
    }

    /**
     * 记录玩家最近登录时间。
     */
    public void markLogin(Long playerId) {
        playerProfileMapper.updateLastLoginTime(playerId, LocalDateTime.now());
    }

    /**
     * 记录玩家最近登出时间。
     */
    public void markLogout(Long playerId) {
        playerProfileMapper.updateLastLogoutTime(playerId, LocalDateTime.now());
    }
}
