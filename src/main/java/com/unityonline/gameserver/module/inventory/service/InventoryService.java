package com.unityonline.gameserver.module.inventory.service;

import com.unityonline.gameserver.common.exception.BizException;
import com.unityonline.gameserver.module.inventory.dto.InventoryItemDto;
import com.unityonline.gameserver.module.inventory.entity.PlayerInventoryEntity;
import com.unityonline.gameserver.module.inventory.mapper.PlayerInventoryMapper;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class InventoryService {

    private final PlayerInventoryMapper playerInventoryMapper;

    public InventoryService(PlayerInventoryMapper playerInventoryMapper) {
        this.playerInventoryMapper = playerInventoryMapper;
    }

    /**
     * 获取玩家背包列表。
     */
    public List<InventoryItemDto> listInventory(Long playerId) {
        return playerInventoryMapper.findByPlayerId(playerId).stream()
                .map(entity -> new InventoryItemDto(entity.getPlayerId(), entity.getItemId(), entity.getItemType(), entity.getQuantity(), entity.getExtJson()))
                .toList();
    }

    /**
     * 增加指定玩家的道具数量。
     */
    @Transactional
    public InventoryItemDto addItem(Long playerId, Long itemId, Integer itemType, Long quantity, String extJson) {
        if (quantity == null || quantity <= 0) {
            throw new BizException(4201, "quantity must be greater than 0");
        }
        PlayerInventoryEntity entity = playerInventoryMapper.findByPlayerIdAndItemId(playerId, itemId).orElseGet(() -> {
            PlayerInventoryEntity newEntity = new PlayerInventoryEntity();
            newEntity.setPlayerId(playerId);
            newEntity.setItemId(itemId);
            newEntity.setItemType(itemType);
            newEntity.setQuantity(0L);
            newEntity.setExtJson("{}");
            return newEntity;
        });
        entity.setItemType(itemType);
        entity.setQuantity(entity.getQuantity() + quantity);
        entity.setExtJson(StringUtils.hasText(extJson) ? extJson : "{}");
        playerInventoryMapper.upsert(entity);
        return new InventoryItemDto(entity.getPlayerId(), entity.getItemId(), entity.getItemType(), entity.getQuantity(), entity.getExtJson());
    }

    /**
     * 消耗指定玩家的道具数量。
     */
    @Transactional
    public InventoryItemDto consumeItem(Long playerId, Long itemId, Long quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BizException(4202, "quantity must be greater than 0");
        }
        PlayerInventoryEntity entity = playerInventoryMapper.findByPlayerIdAndItemId(playerId, itemId)
                .orElseThrow(() -> new BizException(4203, "item not found"));
        long remainQuantity = entity.getQuantity() - quantity;
        if (remainQuantity < 0) {
            throw new BizException(4204, "item quantity is not enough");
        }
        entity.setQuantity(remainQuantity);
        playerInventoryMapper.updateQuantity(playerId, itemId, remainQuantity);
        return new InventoryItemDto(entity.getPlayerId(), entity.getItemId(), entity.getItemType(), entity.getQuantity(), entity.getExtJson());
    }
}
