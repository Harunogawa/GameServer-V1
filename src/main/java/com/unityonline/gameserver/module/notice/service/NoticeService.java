package com.unityonline.gameserver.module.notice.service;

import com.unityonline.gameserver.module.notice.entity.NoticeEntity;
import com.unityonline.gameserver.module.notice.mapper.NoticeMapper;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class NoticeService {

    private final NoticeMapper noticeMapper;

    public NoticeService(NoticeMapper noticeMapper) {
        this.noticeMapper = noticeMapper;
    }

    /**
     * 获取当前有效公告列表。
     */
    public List<NoticeEntity> listNotices() {
        return noticeMapper.findActiveNotices();
    }
}
