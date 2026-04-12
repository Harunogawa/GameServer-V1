package com.unityonline.gameserver.web.devtool;

import java.util.List;

public final class DevToolDtos {

    private DevToolDtos() {
    }

    public record RegisterRequest(
            String username,
            String password,
            String nickname
    ) {
    }

    public record LoginRequest(
            String username,
            String password
    ) {
    }

    public record SessionResponse(
            Long accountId,
            Long playerId,
            String username,
            String nickname,
            String token
    ) {
    }

    public record AddItemRequest(
            Long itemId,
            Integer itemType,
            Long quantity,
            String extJson
    ) {
    }

    public record InventoryItemView(
            Long itemId,
            Integer itemType,
            Long quantity,
            String extJson
    ) {
    }

    public record InventoryView(
            Long playerId,
            List<InventoryItemView> items
    ) {
    }
}
