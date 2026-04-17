package com.unityonline.gameserver.module.notice.mapper;

import com.unityonline.gameserver.common.config.GameConfigJsonLoader;
import com.unityonline.gameserver.module.notice.entity.NoticeEntity;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Repository;

@Repository
public class NoticeJsonRepository {

    private static final String CONFIG_PATH = "classpath:config/tbnotice.json";

    private final GameConfigJsonLoader jsonLoader;
    private final AtomicReference<List<NoticeEntity>> cache = new AtomicReference<>(List.of());

    public NoticeJsonRepository(GameConfigJsonLoader jsonLoader) {
        this.jsonLoader = jsonLoader;
    }

    @PostConstruct
    public void load() {
        List<NoticeEntity> configs = jsonLoader.loadList(CONFIG_PATH).stream()
                .map(this::toEntity)
                .sorted(Comparator.comparing(NoticeEntity::getPriority).reversed()
                        .thenComparing(NoticeEntity::getNoticeId, Comparator.reverseOrder()))
                .toList();
        cache.set(configs);
    }

    public List<NoticeEntity> findActiveNotices() {
        LocalDateTime now = LocalDateTime.now();
        return cache.get().stream()
                .filter(entity -> entity.getStatus() != null && entity.getStatus() == 1)
                .filter(entity -> entity.getStartTime() == null || !entity.getStartTime().isAfter(now))
                .filter(entity -> entity.getEndTime() == null || !entity.getEndTime().isBefore(now))
                .toList();
    }

    private NoticeEntity toEntity(Map<String, Object> row) {
        NoticeEntity entity = new NoticeEntity();
        entity.setNoticeId(jsonLoader.toLong(row.get("notice_id")));
        entity.setTitle(jsonLoader.toText(row.get("title")));
        entity.setContent(jsonLoader.toText(row.get("content")));
        entity.setPriority(jsonLoader.toInt(row.get("priority")));
        entity.setStatus(jsonLoader.toInt(row.get("status")));
        entity.setStartTime(jsonLoader.toDateTime(row.get("start_time")));
        entity.setEndTime(jsonLoader.toDateTime(row.get("end_time")));
        return entity;
    }
}
