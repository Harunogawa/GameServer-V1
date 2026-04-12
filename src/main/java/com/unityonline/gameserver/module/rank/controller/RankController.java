package com.unityonline.gameserver.module.rank.controller;

import com.unityonline.gameserver.common.context.LoginPlayerContext;
import com.unityonline.gameserver.common.exception.BizException;
import com.unityonline.gameserver.common.proto.ProtoResponseBuilder;
import com.unityonline.gameserver.module.rank.dto.RankEntryDto;
import com.unityonline.gameserver.module.rank.service.RankService;
import com.unityonline.gameserver.proto.game.rank.MyRankRequest;
import com.unityonline.gameserver.proto.game.rank.MyRankResponse;
import com.unityonline.gameserver.proto.game.rank.RankEntry;
import com.unityonline.gameserver.proto.game.rank.RankQueryRequest;
import com.unityonline.gameserver.proto.game.rank.RankQueryResponse;
import com.unityonline.gameserver.proto.game.rank.RankReportRequest;
import com.unityonline.gameserver.proto.game.rank.RankReportResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rank")
public class RankController {

    private static final String PROTOBUF_VALUE = "application/x-protobuf";

    private final RankService rankService;
    private final ProtoResponseBuilder protoResponseBuilder;

    public RankController(RankService rankService, ProtoResponseBuilder protoResponseBuilder) {
        this.rankService = rankService;
        this.protoResponseBuilder = protoResponseBuilder;
    }

    /**
     * 上报排行榜分数。
     */
    @PostMapping(value = "/report", consumes = PROTOBUF_VALUE, produces = PROTOBUF_VALUE)
    public RankReportResponse report(@RequestBody RankReportRequest request) {
        Long playerId = requireLoginAndMatch(request.getPlayerId());
        RankEntryDto entry = rankService.reportScore(request.getRankType(), playerId, request.getScore());
        return RankReportResponse.newBuilder()
                .setResult(protoResponseBuilder.success())
                .setScore(entry.score())
                .setRankNo(entry.rankNo())
                .build();
    }

    /**
     * 分页查询排行榜。
     */
    @PostMapping(value = "/list", consumes = PROTOBUF_VALUE, produces = PROTOBUF_VALUE)
    public RankQueryResponse list(@RequestBody RankQueryRequest request) {
        RankQueryResponse.Builder builder = RankQueryResponse.newBuilder().setResult(protoResponseBuilder.success());
        int pageNo = request.getPageNo() <= 0 ? 1 : request.getPageNo();
        int pageSize = request.getPageSize() <= 0 ? 20 : request.getPageSize();
        rankService.queryRankList(request.getRankType(), pageNo, pageSize).stream()
                .map(this::toProto)
                .forEach(builder::addEntries);
        return builder.build();
    }

    /**
     * 查询自己的排行榜名次。
     */
    @PostMapping(value = "/my", consumes = PROTOBUF_VALUE, produces = PROTOBUF_VALUE)
    public MyRankResponse myRank(@RequestBody MyRankRequest request) {
        Long playerId = requireLoginAndMatch(request.getPlayerId());
        RankEntryDto entry = rankService.queryMyRank(request.getRankType(), playerId);
        return MyRankResponse.newBuilder()
                .setResult(protoResponseBuilder.success())
                .setEntry(toProto(entry))
                .build();
    }

    /**
     * 将排行榜 DTO 转成 protobuf 对象。
     */
    private RankEntry toProto(RankEntryDto dto) {
        return RankEntry.newBuilder()
                .setRankNo(dto.rankNo())
                .setPlayerId(dto.playerId())
                .setNickname(dto.nickname())
                .setScore(dto.score())
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
