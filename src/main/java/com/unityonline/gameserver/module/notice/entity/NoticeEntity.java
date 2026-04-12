package com.unityonline.gameserver.module.notice.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class NoticeEntity {

    private Long id;
    private Long noticeId;
    private String title;
    private String content;
    private Integer priority;
    private Integer status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
