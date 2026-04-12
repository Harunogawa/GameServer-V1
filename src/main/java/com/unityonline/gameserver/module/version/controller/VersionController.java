package com.unityonline.gameserver.module.version.controller;

import com.unityonline.gameserver.common.proto.ProtoResponseBuilder;
import com.unityonline.gameserver.module.version.entity.VersionConfigEntity;
import com.unityonline.gameserver.module.version.service.VersionService;
import com.unityonline.gameserver.proto.game.version.ResourceVersionRequest;
import com.unityonline.gameserver.proto.game.version.ResourceVersionResponse;
import com.unityonline.gameserver.proto.game.version.VersionCheckRequest;
import com.unityonline.gameserver.proto.game.version.VersionCheckResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/version")
public class VersionController {

    private static final String PROTOBUF_VALUE = "application/x-protobuf";

    private final VersionService versionService;
    private final ProtoResponseBuilder protoResponseBuilder;

    public VersionController(VersionService versionService, ProtoResponseBuilder protoResponseBuilder) {
        this.versionService = versionService;
        this.protoResponseBuilder = protoResponseBuilder;
    }

    /**
     * 获取客户端版本信息。
     */
    @PostMapping(value = "/client", consumes = PROTOBUF_VALUE, produces = PROTOBUF_VALUE)
    public VersionCheckResponse clientVersion(@RequestBody VersionCheckRequest request) {
        VersionConfigEntity entity = versionService.getVersionInfo(request.getPlatform());
        boolean needUpdate = compareVersion(request.getClientVersion(), entity.getLatestClientVersion()) < 0;
        boolean forceUpdate = compareVersion(request.getClientVersion(), entity.getMinClientVersion()) < 0 || entity.getForceUpdate() == 1;
        return VersionCheckResponse.newBuilder()
                .setResult(protoResponseBuilder.success())
                .setNeedUpdate(needUpdate)
                .setForceUpdate(forceUpdate)
                .setLatestVersion(entity.getLatestClientVersion())
                .setDownloadUrl(entity.getDownloadUrl())
                .setNotice(entity.getNotice() == null ? "" : entity.getNotice())
                .setResourceVersion(entity.getResourceVersion())
                .build();
    }

    /**
     * 获取资源版本信息。
     */
    @PostMapping(value = "/resource", consumes = PROTOBUF_VALUE, produces = PROTOBUF_VALUE)
    public ResourceVersionResponse resourceVersion(@RequestBody ResourceVersionRequest request) {
        VersionConfigEntity entity = versionService.getResourceVersion(request.getPlatform());
        return ResourceVersionResponse.newBuilder()
                .setResult(protoResponseBuilder.success())
                .setResourceVersion(entity.getResourceVersion())
                .setResourceUrl(entity.getResourceUrl())
                .build();
    }

    /**
     * 比较两个版本字符串大小。
     */
    private int compareVersion(String source, String target) {
        String safeSource = (source == null || source.isBlank()) ? "0" : source;
        String safeTarget = (target == null || target.isBlank()) ? "0" : target;
        String[] left = safeSource.split("\\.");
        String[] right = safeTarget.split("\\.");
        int length = Math.max(left.length, right.length);
        for (int index = 0; index < length; index++) {
            int leftValue = index < left.length ? Integer.parseInt(left[index]) : 0;
            int rightValue = index < right.length ? Integer.parseInt(right[index]) : 0;
            if (leftValue != rightValue) {
                return Integer.compare(leftValue, rightValue);
            }
        }
        return 0;
    }
}
