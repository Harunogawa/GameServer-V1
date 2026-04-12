package com.unityonline.gameserver.module.stage.controller;

import com.unityonline.gameserver.common.context.LoginPlayerContext;
import com.unityonline.gameserver.common.exception.BizException;
import com.unityonline.gameserver.common.proto.ProtoResponseBuilder;
import com.unityonline.gameserver.module.stage.dto.StageResultDto;
import com.unityonline.gameserver.module.stage.service.StageService;
import com.unityonline.gameserver.proto.game.stage.StageInfo;
import com.unityonline.gameserver.proto.game.stage.StageListRequest;
import com.unityonline.gameserver.proto.game.stage.StageListResponse;
import com.unityonline.gameserver.proto.game.stage.StageReportRequest;
import com.unityonline.gameserver.proto.game.stage.StageReportResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stage")
public class StageController {

    private static final String PROTOBUF_VALUE = "application/x-protobuf";

    private final StageService stageService;
    private final ProtoResponseBuilder protoResponseBuilder;

    public StageController(StageService stageService, ProtoResponseBuilder protoResponseBuilder) {
        this.stageService = stageService;
        this.protoResponseBuilder = protoResponseBuilder;
    }

    /**
     * 获取玩家关卡列表。
     */
    @PostMapping(value = "/list", consumes = PROTOBUF_VALUE, produces = PROTOBUF_VALUE)
    public StageListResponse list(@RequestBody StageListRequest request) {
        Long playerId = requireLoginAndMatch(request.getPlayerId());
        StageListResponse.Builder builder = StageListResponse.newBuilder().setResult(protoResponseBuilder.success());
        stageService.listStages(playerId).stream().map(this::toProto).forEach(builder::addStages);
        return builder.build();
    }

    /**
     * 上报关卡结算结果。
     */
    @PostMapping(value = "/report", consumes = PROTOBUF_VALUE, produces = PROTOBUF_VALUE)
    public StageReportResponse report(@RequestBody StageReportRequest request) {
        Long playerId = requireLoginAndMatch(request.getPlayerId());
        StageResultDto result = stageService.reportStage(playerId, request.getStageId(), request.getStarCount(), request.getCleared(), request.getScore());
        return StageReportResponse.newBuilder()
                .setResult(protoResponseBuilder.success())
                .setStage(toProto(result))
                .setFirstClearReward(result.firstClearReward())
                .build();
    }

    /**
     * 将关卡 DTO 转成 protobuf 对象。
     */
    private StageInfo toProto(StageResultDto dto) {
        return StageInfo.newBuilder()
                .setStageId(dto.stageId())
                .setStageName(dto.stageName())
                .setStarCount(dto.starCount())
                .setCleared(dto.cleared())
                .setBestScore(dto.bestScore())
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
