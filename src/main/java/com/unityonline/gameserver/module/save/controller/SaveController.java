package com.unityonline.gameserver.module.save.controller;

import com.unityonline.gameserver.common.context.LoginPlayerContext;
import com.unityonline.gameserver.common.exception.BizException;
import com.unityonline.gameserver.common.proto.ProtoResponseBuilder;
import com.unityonline.gameserver.module.save.dto.SaveGameDto;
import com.unityonline.gameserver.module.save.service.SaveService;
import com.unityonline.gameserver.proto.game.save.PullSaveRequest;
import com.unityonline.gameserver.proto.game.save.PullSaveResponse;
import com.unityonline.gameserver.proto.game.save.UploadSaveRequest;
import com.unityonline.gameserver.proto.game.save.UploadSaveResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/save")
public class SaveController {

    private static final String PROTOBUF_VALUE = "application/x-protobuf";

    private final SaveService saveService;
    private final ProtoResponseBuilder protoResponseBuilder;

    public SaveController(SaveService saveService, ProtoResponseBuilder protoResponseBuilder) {
        this.saveService = saveService;
        this.protoResponseBuilder = protoResponseBuilder;
    }

    /**
     * 上传玩家存档。
     */
    @PostMapping(value = "/upload", consumes = PROTOBUF_VALUE, produces = PROTOBUF_VALUE)
    public UploadSaveResponse upload(@RequestBody UploadSaveRequest request) {
        Long playerId = requireLoginAndMatch(request.getPlayerId());
        SaveGameDto saveGameDto = saveService.uploadSave(playerId, request.getSlot(), request.getSaveDataJson(), request.getSaveVersion());
        return UploadSaveResponse.newBuilder()
                .setResult(protoResponseBuilder.success())
                .setPlayerId(saveGameDto.playerId())
                .setSlot(saveGameDto.slot())
                .build();
    }

    /**
     * 拉取玩家存档。
     */
    @PostMapping(value = "/pull", consumes = PROTOBUF_VALUE, produces = PROTOBUF_VALUE)
    public PullSaveResponse pull(@RequestBody PullSaveRequest request) {
        Long playerId = requireLoginAndMatch(request.getPlayerId());
        SaveGameDto saveGameDto = saveService.pullSave(playerId, request.getSlot());
        return PullSaveResponse.newBuilder()
                .setResult(protoResponseBuilder.success())
                .setPlayerId(saveGameDto.playerId())
                .setSlot(saveGameDto.slot())
                .setSaveDataJson(saveGameDto.saveDataJson())
                .setSaveVersion(saveGameDto.saveVersion())
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
