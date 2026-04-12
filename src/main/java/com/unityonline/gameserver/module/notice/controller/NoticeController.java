package com.unityonline.gameserver.module.notice.controller;

import com.unityonline.gameserver.common.proto.ProtoResponseBuilder;
import com.unityonline.gameserver.module.notice.entity.NoticeEntity;
import com.unityonline.gameserver.module.notice.service.NoticeService;
import com.unityonline.gameserver.proto.game.notice.NoticeInfo;
import com.unityonline.gameserver.proto.game.notice.NoticeListRequest;
import com.unityonline.gameserver.proto.game.notice.NoticeListResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    private static final String PROTOBUF_VALUE = "application/x-protobuf";

    private final NoticeService noticeService;
    private final ProtoResponseBuilder protoResponseBuilder;

    public NoticeController(NoticeService noticeService, ProtoResponseBuilder protoResponseBuilder) {
        this.noticeService = noticeService;
        this.protoResponseBuilder = protoResponseBuilder;
    }

    /**
     * 获取有效公告列表。
     */
    @PostMapping(value = "/list", consumes = PROTOBUF_VALUE, produces = PROTOBUF_VALUE)
    public NoticeListResponse list(@RequestBody NoticeListRequest request) {
        NoticeListResponse.Builder builder = NoticeListResponse.newBuilder().setResult(protoResponseBuilder.success());
        noticeService.listNotices().stream().map(this::toProto).forEach(builder::addNotices);
        return builder.build();
    }

    /**
     * 将公告实体转成 protobuf 对象。
     */
    private NoticeInfo toProto(NoticeEntity entity) {
        return NoticeInfo.newBuilder()
                .setNoticeId(entity.getNoticeId())
                .setTitle(entity.getTitle())
                .setContent(entity.getContent())
                .setPriority(entity.getPriority())
                .setStartTime(entity.getStartTime() == null ? 0L : entity.getStartTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli())
                .setEndTime(entity.getEndTime() == null ? 0L : entity.getEndTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli())
                .build();
    }
}
