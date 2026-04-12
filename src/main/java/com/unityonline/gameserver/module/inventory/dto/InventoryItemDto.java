package com.unityonline.gameserver.module.inventory.dto;

public record InventoryItemDto(
        Long playerId,
        Long itemId,
        Integer itemType,
        Long quantity,
        String extJson
) {
}
