package com.unityonline.gameserver.module.notice.mapper;

import com.unityonline.gameserver.module.notice.entity.NoticeEntity;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class NoticeMapper {

    private final NoticeJsonRepository repository;

    public NoticeMapper(NoticeJsonRepository repository) {
        this.repository = repository;
    }

    public List<NoticeEntity> findActiveNotices() {
        return repository.findActiveNotices();
    }
}
