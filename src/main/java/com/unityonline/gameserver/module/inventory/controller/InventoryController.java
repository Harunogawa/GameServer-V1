package com.unityonline.gameserver.module.inventory.controller;

import com.unityonline.gameserver.common.context.LoginPlayerContext;
import com.unityonline.gameserver.common.exception.BizException;
import com.unityonline.gameserver.common.proto.ProtoResponseBuilder;
import com.unityonline.gameserver.module.inventory.dto.InventoryItemDto;
import com.unityonline.gameserver.module.inventory.service.InventoryService;
import com.unityonline.gameserver.proto.game.inventory.AddItemRequest;
import com.unityonline.gameserver.proto.game.inventory.AddItemResponse;
import com.unityonline.gameserver.proto.game.inventory.ConsumeItemRequest;
import com.unityonline.gameserver.proto.game.inventory.ConsumeItemResponse;
import com.unityonline.gameserver.proto.game.inventory.InventoryItem;
import com.unityonline.gameserver.proto.game.inventory.InventoryQueryRequest;
import com.unityonline.gameserver.proto.game.inventory.InventoryQueryResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private static final String PROTOBUF_VALUE = "application/x-protobuf";

    private final InventoryService inventoryService;
    private final ProtoResponseBuilder protoResponseBuilder;

    public InventoryController(InventoryService inventoryService, ProtoResponseBuilder protoResponseBuilder) {
        this.inventoryService = inventoryService;
        this.protoResponseBuilder = protoResponseBuilder;
    }

    /**
     * 获取背包列表。
     */
    @PostMapping(value = "/list", consumes = PROTOBUF_VALUE, produces = PROTOBUF_VALUE)
    public InventoryQueryResponse list(@RequestBody InventoryQueryRequest request) {
        Long playerId = requireLoginAndMatch(request.getPlayerId());
        InventoryQueryResponse.Builder builder = InventoryQueryResponse.newBuilder()
                .setResult(protoResponseBuilder.success());
        inventoryService.listInventory(playerId).stream()
                .map(this::toProtoItem)
                .forEach(builder::addItems);
        return builder.build();
    }

    /**
     * 增加背包道具。
     */
    @PostMapping(value = "/add", consumes = PROTOBUF_VALUE, produces = PROTOBUF_VALUE)
    public AddItemResponse add(@RequestBody AddItemRequest request) {
        Long playerId = requireLoginAndMatch(request.getPlayerId());
        InventoryItemDto item = inventoryService.addItem(playerId, request.getItemId(), request.getItemType(), request.getQuantity(), request.getExtJson());
        return AddItemResponse.newBuilder()
                .setResult(protoResponseBuilder.success())
                .setItem(toProtoItem(item))
                .build();
    }

    /**
     * 消耗背包道具。
     */
    @PostMapping(value = "/consume", consumes = PROTOBUF_VALUE, produces = PROTOBUF_VALUE)
    public ConsumeItemResponse consume(@RequestBody ConsumeItemRequest request) {
        Long playerId = requireLoginAndMatch(request.getPlayerId());
        InventoryItemDto item = inventoryService.consumeItem(playerId, request.getItemId(), request.getQuantity());
        return ConsumeItemResponse.newBuilder()
                .setResult(protoResponseBuilder.success())
                .setItem(toProtoItem(item))
                .build();
    }

    /**
     * 将 DTO 转换为 protobuf 道具对象。
     */
    private InventoryItem toProtoItem(InventoryItemDto dto) {
        return InventoryItem.newBuilder()
                .setItemId(dto.itemId())
                .setItemType(dto.itemType())
                .setQuantity(dto.quantity())
                .build();
    }

    /**
     * 检查请求中的玩家 ID 是否与登录上下文一致。
     */
    private Long requireLoginAndMatch(long requestPlayerId) {
        Long loginPlayerId = LoginPlayerContext.getPlayerId();
        if (loginPlayerId == null) {
            throw new BizException(4011, "login required");
        }
        if (requestPlayerId != 0 && !loginPlayerId.equals(requestPlayerId)) {
            throw new BizException(4012, "cannot operate other player");
        }
        return loginPlayerId;
    }
}
