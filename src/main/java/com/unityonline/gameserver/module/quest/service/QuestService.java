package com.unityonline.gameserver.module.quest.service;

import com.unityonline.gameserver.common.constant.GameConstants;
import com.unityonline.gameserver.common.exception.BizException;
import com.unityonline.gameserver.module.inventory.service.InventoryService;
import com.unityonline.gameserver.module.quest.dto.QuestDto;
import com.unityonline.gameserver.module.quest.entity.PlayerQuestEntity;
import com.unityonline.gameserver.module.quest.entity.QuestConfigEntity;
import com.unityonline.gameserver.module.quest.mapper.PlayerQuestMapper;
import com.unityonline.gameserver.module.quest.mapper.QuestConfigMapper;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestService {

    private final QuestConfigMapper questConfigMapper;
    private final PlayerQuestMapper playerQuestMapper;
    private final InventoryService inventoryService;

    public QuestService(QuestConfigMapper questConfigMapper,
                        PlayerQuestMapper playerQuestMapper,
                        InventoryService inventoryService) {
        this.questConfigMapper = questConfigMapper;
        this.playerQuestMapper = playerQuestMapper;
        this.inventoryService = inventoryService;
    }

    /**
     * 初始化玩家任务状态，为所有启用中的任务创建进度记录。
     */
    @Transactional
    public void initializePlayerQuests(Long playerId) {
        Map<Long, PlayerQuestEntity> existingMap = playerQuestMapper.findByPlayerId(playerId).stream()
                .collect(java.util.stream.Collectors.toMap(PlayerQuestEntity::getQuestId, Function.identity()));
        for (QuestConfigEntity config : questConfigMapper.findActiveList()) {
            if (existingMap.containsKey(config.getQuestId())) {
                continue;
            }
            PlayerQuestEntity entity = new PlayerQuestEntity();
            entity.setPlayerId(playerId);
            entity.setQuestId(config.getQuestId());
            entity.setQuestStatus(GameConstants.QUEST_STATUS_IN_PROGRESS);
            entity.setProgress(0);
            entity.setRewardClaimed(0);
            playerQuestMapper.insert(entity);
        }
    }

    /**
     * 获取玩家任务列表。
     */
    public List<QuestDto> listQuests(Long playerId) {
        initializePlayerQuests(playerId);
        Map<Long, QuestConfigEntity> configMap = questConfigMapper.findActiveList().stream()
                .collect(java.util.stream.Collectors.toMap(QuestConfigEntity::getQuestId, Function.identity()));
        return playerQuestMapper.findByPlayerId(playerId).stream()
                .filter(playerQuest -> configMap.containsKey(playerQuest.getQuestId()))
                .sorted(Comparator.comparing(PlayerQuestEntity::getQuestId))
                .map(playerQuest -> toDto(playerQuest, configMap.get(playerQuest.getQuestId())))
                .toList();
    }

    /**
     * 手动更新某个任务进度。
     */
    @Transactional
    public QuestDto updateQuestProgress(Long playerId, Long questId, Integer progressDelta) {
        QuestConfigEntity config = questConfigMapper.findByQuestId(questId)
                .orElseThrow(() -> new BizException(4301, "quest config not found"));
        PlayerQuestEntity playerQuest = playerQuestMapper.findByPlayerIdAndQuestId(playerId, questId)
                .orElseThrow(() -> new BizException(4302, "player quest not found"));
        int nextProgress = Math.min(config.getTargetValue(), playerQuest.getProgress() + Math.max(progressDelta, 0));
        int nextStatus = nextProgress >= config.getTargetValue() ? GameConstants.QUEST_STATUS_COMPLETED : GameConstants.QUEST_STATUS_IN_PROGRESS;
        playerQuestMapper.updateProgress(playerId, questId, nextProgress, nextStatus);
        playerQuest.setProgress(nextProgress);
        playerQuest.setQuestStatus(nextStatus);
        return toDto(playerQuest, config);
    }

    /**
     * 按事件类型推进相关任务进度。
     */
    @Transactional
    public void advanceByQuestType(Long playerId, Integer questType, Integer progressDelta) {
        if (progressDelta == null || progressDelta <= 0) {
            return;
        }
        initializePlayerQuests(playerId);
        List<QuestConfigEntity> configs = questConfigMapper.findActiveList().stream()
                .filter(config -> config.getQuestType().equals(questType))
                .toList();
        for (QuestConfigEntity config : configs) {
            PlayerQuestEntity playerQuest = playerQuestMapper.findByPlayerIdAndQuestId(playerId, config.getQuestId())
                    .orElseThrow(() -> new BizException(4302, "player quest not found"));
            if (playerQuest.getRewardClaimed() != null && playerQuest.getRewardClaimed() == 1) {
                continue;
            }
            int nextProgress = Math.min(config.getTargetValue(), playerQuest.getProgress() + progressDelta);
            int nextStatus = nextProgress >= config.getTargetValue() ? GameConstants.QUEST_STATUS_COMPLETED : GameConstants.QUEST_STATUS_IN_PROGRESS;
            playerQuestMapper.updateProgress(playerId, config.getQuestId(), nextProgress, nextStatus);
        }
    }

    /**
     * 领取任务奖励，并将奖励发放到玩家背包。
     */
    @Transactional
    public QuestDto claimReward(Long playerId, Long questId) {
        QuestConfigEntity config = questConfigMapper.findByQuestId(questId)
                .orElseThrow(() -> new BizException(4301, "quest config not found"));
        PlayerQuestEntity playerQuest = playerQuestMapper.findByPlayerIdAndQuestId(playerId, questId)
                .orElseThrow(() -> new BizException(4302, "player quest not found"));
        if (playerQuest.getQuestStatus() == null || playerQuest.getQuestStatus() != GameConstants.QUEST_STATUS_COMPLETED) {
            throw new BizException(4303, "quest is not completed");
        }
        if (playerQuest.getRewardClaimed() != null && playerQuest.getRewardClaimed() == 1) {
            throw new BizException(4304, "quest reward already claimed");
        }
        if (config.getRewardQuantity() != null && config.getRewardQuantity() > 0) {
            inventoryService.addItem(playerId, config.getRewardItemId(), config.getRewardItemType(), config.getRewardQuantity(), "{\"source\":\"quest\"}");
        }
        playerQuestMapper.updateRewardClaimed(playerId, questId, 1);
        playerQuest.setRewardClaimed(1);
        return toDto(playerQuest, config);
    }

    /**
     * 将任务配置与玩家进度合并成返回 DTO。
     */
    private QuestDto toDto(PlayerQuestEntity playerQuest, QuestConfigEntity config) {
        return new QuestDto(
                config.getQuestId(),
                config.getQuestName(),
                config.getQuestDesc(),
                playerQuest.getQuestStatus(),
                playerQuest.getProgress(),
                config.getTargetValue(),
                playerQuest.getRewardClaimed() != null && playerQuest.getRewardClaimed() == 1
        );
    }
}
