package com.unityonline.gameserver.module.stage.service;

import com.unityonline.gameserver.common.constant.GameConstants;
import com.unityonline.gameserver.common.exception.BizException;
import com.unityonline.gameserver.module.inventory.service.InventoryService;
import com.unityonline.gameserver.module.quest.service.QuestService;
import com.unityonline.gameserver.module.stage.dto.StageResultDto;
import com.unityonline.gameserver.module.stage.entity.PlayerStageEntity;
import com.unityonline.gameserver.module.stage.entity.StageConfigEntity;
import com.unityonline.gameserver.module.stage.mapper.PlayerStageMapper;
import com.unityonline.gameserver.module.stage.mapper.StageConfigMapper;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StageService {

    private final StageConfigMapper stageConfigMapper;
    private final PlayerStageMapper playerStageMapper;
    private final InventoryService inventoryService;
    private final QuestService questService;

    public StageService(StageConfigMapper stageConfigMapper,
                        PlayerStageMapper playerStageMapper,
                        InventoryService inventoryService,
                        QuestService questService) {
        this.stageConfigMapper = stageConfigMapper;
        this.playerStageMapper = playerStageMapper;
        this.inventoryService = inventoryService;
        this.questService = questService;
    }

    /**
     * 获取玩家关卡列表。
     */
    public List<StageResultDto> listStages(Long playerId) {
        Map<Long, PlayerStageEntity> playerStageMap = playerStageMapper.findByPlayerId(playerId).stream()
                .collect(java.util.stream.Collectors.toMap(PlayerStageEntity::getStageId, Function.identity()));
        return stageConfigMapper.findActiveList().stream()
                .sorted(Comparator.comparing(StageConfigEntity::getStageId))
                .map(config -> {
                    PlayerStageEntity playerStage = playerStageMap.get(config.getStageId());
                    return new StageResultDto(
                            config.getStageId(),
                            config.getStageName(),
                            playerStage == null ? 0 : playerStage.getStarCount(),
                            playerStage != null && playerStage.getClearStatus() == 1,
                            playerStage == null ? 0L : playerStage.getBestScore(),
                            false
                    );
                })
                .toList();
    }

    /**
     * 上报关卡结算，更新最好成绩、最高星级、首次通关奖励和任务进度。
     */
    @Transactional
    public StageResultDto reportStage(Long playerId, Long stageId, Integer starCount, boolean cleared, Long score) {
        StageConfigEntity config = stageConfigMapper.findByStageId(stageId)
                .orElseThrow(() -> new BizException(4401, "stage config not found"));
        PlayerStageEntity current = playerStageMapper.findByPlayerIdAndStageId(playerId, stageId).orElse(null);

        int previousStar = current == null ? 0 : current.getStarCount();
        boolean previousCleared = current != null && current.getClearStatus() == 1;
        long previousBestScore = current == null ? 0L : current.getBestScore();
        int mergedStar = Math.max(previousStar, Math.max(starCount, 0));
        long mergedScore = Math.max(previousBestScore, Math.max(score, 0L));
        boolean mergedCleared = previousCleared || cleared;

        PlayerStageEntity entity = new PlayerStageEntity();
        entity.setPlayerId(playerId);
        entity.setStageId(stageId);
        entity.setStarCount(mergedStar);
        entity.setClearStatus(mergedCleared ? 1 : 0);
        entity.setBestScore(mergedScore);
        playerStageMapper.upsert(entity);

        boolean firstClearReward = mergedCleared && !previousCleared;
        int starDelta = Math.max(mergedStar - previousStar, 0);
        if (firstClearReward && config.getRewardQuantity() != null && config.getRewardQuantity() > 0) {
            inventoryService.addItem(playerId, config.getRewardItemId(), config.getRewardItemType(), config.getRewardQuantity(), "{\"source\":\"stage\"}");
        }
        if (mergedCleared && !previousCleared) {
            questService.advanceByQuestType(playerId, GameConstants.QUEST_TYPE_CLEAR_STAGE, 1);
        }
        if (starDelta > 0) {
            questService.advanceByQuestType(playerId, GameConstants.QUEST_TYPE_TOTAL_STAR, starDelta);
        }

        return new StageResultDto(stageId, config.getStageName(), mergedStar, mergedCleared, mergedScore, firstClearReward);
    }
}
