package com.unityonline.gameserver.module.version.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class VersionConfigEntity {

    private Long id;
    private Integer versionCode;
    private String versionName;
    private String platform;
    private String minClientVersion;
    private String latestClientVersion;
    private String resourceVersion;
    private String resourceUrl;
    private String downloadUrl;
    private Integer forceUpdate;
    private String notice;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
