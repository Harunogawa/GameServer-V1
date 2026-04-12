package com.unityonline.gameserver.module.quest.controller;

import com.unityonline.gameserver.common.context.LoginPlayerContext;
import com.unityonline.gameserver.common.exception.BizException;
import com.unityonline.gameserver.common.proto.ProtoResponseBuilder;
import com.unityonline.gameserver.module.quest.dto.QuestDto;
import com.unityonline.gameserver.module.quest.service.QuestService;
import com.unityonline.gameserver.proto.game.quest.ClaimQuestRewardRequest;
import com.unityonline.gameserver.proto.game.quest.ClaimQuestRewardResponse;
import com.unityonline.gameserver.proto.game.quest.QuestInfo;
import com.unityonline.gameserver.proto.game.quest.QuestListRequest;
import com.unityonline.gameserver.proto.game.quest.QuestListResponse;
import com.unityonline.gameserver.proto.game.quest.UpdateQuestProgressRequest;
import com.unityonline.gameserver.proto.game.quest.UpdateQuestProgressResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quest")
public class QuestController {

    private static final String PROTOBUF_VALUE = "application/x-protobuf";

    private final QuestService questService;
    private final ProtoResponseBuilder protoResponseBuilder;

    public QuestController(QuestService questService, ProtoResponseBuilder protoResponseBuilder) {
        this.questService = questService;
        this.protoResponseBuilder = protoResponseBuilder;
    }

    /**
     * 获取当前登录玩家的任务列表。
     */
    @PostMapping(value = "/list", consumes = PROTOBUF_VALUE, produces = PROTOBUF_VALUE)
    public QuestListResponse list(@RequestBody QuestListRequest request) {
        Long playerId = requireLoginAndMatch(request.getPlayerId());
        QuestListResponse.Builder builder = QuestListResponse.newBuilder().setResult(protoResponseBuilder.success());
        questService.listQuests(playerId).stream().map(this::toProto).forEach(builder::addQuests);
        return builder.build();
    }

    /**
     * 手动更新某个任务进度。
     */
    @PostMapping(value = "/progress/update", consumes = PROTOBUF_VALUE, produces = PROTOBUF_VALUE)
    public UpdateQuestProgressResponse updateProgress(@RequestBody UpdateQuestProgressRequest request) {
        Long playerId = requireLoginAndMatch(request.getPlayerId());
        QuestDto quest = questService.updateQuestProgress(playerId, request.getQuestId(), request.getProgressDelta());
        return UpdateQuestProgressResponse.newBuilder()
                .setResult(protoResponseBuilder.success())
                .setQuest(toProto(quest))
                .build();
    }

    /**
     * 领取任务奖励。
     */
    @PostMapping(value = "/reward/claim", consumes = PROTOBUF_VALUE, produces = PROTOBUF_VALUE)
    public ClaimQuestRewardResponse claimReward(@RequestBody ClaimQuestRewardRequest request) {
        Long playerId = requireLoginAndMatch(request.getPlayerId());
        QuestDto quest = questService.claimReward(playerId, request.getQuestId());
        return ClaimQuestRewardResponse.newBuilder()
                .setResult(protoResponseBuilder.success())
                .setQuest(toProto(quest))
                .build();
    }

    /**
     * 将任务 DTO 转成 protobuf 对象。
     */
    private QuestInfo toProto(QuestDto dto) {
        return QuestInfo.newBuilder()
                .setQuestId(dto.questId())
                .setQuestName(dto.questName())
                .setQuestDesc(dto.questDesc())
                .setStatus(dto.status())
                .setProgress(dto.progress())
                .setTargetValue(dto.targetValue())
                .setRewardClaimed(dto.rewardClaimed())
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
