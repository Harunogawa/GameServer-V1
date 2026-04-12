package com.unityonline.gameserver.module.save.service;

import com.unityonline.gameserver.common.exception.BizException;
import com.unityonline.gameserver.module.save.dto.SaveGameDto;
import com.unityonline.gameserver.module.save.entity.PlayerSaveEntity;
import com.unityonline.gameserver.module.save.mapper.PlayerSaveMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class SaveService {

    private final PlayerSaveMapper playerSaveMapper;

    public SaveService(PlayerSaveMapper playerSaveMapper) {
        this.playerSaveMapper = playerSaveMapper;
    }

    /**
     * 上传玩家存档数据。
     */
    @Transactional
    public SaveGameDto uploadSave(Long playerId, Integer slot, String saveDataJson, String saveVersion) {
        if (!StringUtils.hasText(saveDataJson)) {
            throw new BizException(4101, "save data cannot be empty");
        }
        if (!StringUtils.hasText(saveVersion)) {
            throw new BizException(4102, "save version cannot be empty");
        }
        PlayerSaveEntity entity = new PlayerSaveEntity();
        entity.setPlayerId(playerId);
        entity.setSaveSlot(slot);
        entity.setSaveDataJson(saveDataJson);
        entity.setSaveVersion(saveVersion);
        playerSaveMapper.upsert(entity);
        return new SaveGameDto(playerId, slot, saveDataJson, saveVersion);
    }

    /**
     * 拉取玩家存档数据。
     */
    public SaveGameDto pullSave(Long playerId, Integer slot) {
        PlayerSaveEntity entity = playerSaveMapper.findByPlayerIdAndSlot(playerId, slot)
                .orElseThrow(() -> new BizException(4103, "save not found"));
        return new SaveGameDto(entity.getPlayerId(), entity.getSaveSlot(), entity.getSaveDataJson(), entity.getSaveVersion());
    }
}
