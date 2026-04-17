package com.unityonline.gameserver.module.quest.mapper;

import com.unityonline.gameserver.common.config.GameConfigJsonLoader;
import com.unityonline.gameserver.module.quest.entity.QuestConfigEntity;
import jakarta.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Repository;

@Repository
public class QuestConfigJsonRepository {

    private static final String CONFIG_PATH = "classpath:config/tbquestconfig.json";

    private final GameConfigJsonLoader jsonLoader;
    private final AtomicReference<List<QuestConfigEntity>> cache = new AtomicReference<>(List.of());

    public QuestConfigJsonRepository(GameConfigJsonLoader jsonLoader) {
        this.jsonLoader = jsonLoader;
    }

    @PostConstruct
    public void load() {
        List<QuestConfigEntity> configs = jsonLoader.loadList(CONFIG_PATH).stream()
                .map(this::toEntity)
                .sorted(Comparator.comparing(QuestConfigEntity::getQuestId))
                .toList();
        cache.set(configs);
    }

    public List<QuestConfigEntity> findAll() {
        return cache.get();
    }

    public Optional<QuestConfigEntity> findByQuestId(Long questId) {
        return cache.get().stream()
                .filter(config -> config.getQuestId().equals(questId))
                .findFirst();
    }

    private QuestConfigEntity toEntity(Map<String, Object> row) {
        QuestConfigEntity entity = new QuestConfigEntity();
        entity.setQuestId(jsonLoader.toLong(row.get("quest_id")));
        entity.setQuestName(jsonLoader.toText(row.get("quest_name")));
        entity.setQuestDesc(jsonLoader.toText(row.get("quest_desc")));
        entity.setQuestType(jsonLoader.toInt(row.get("quest_type")));
        entity.setTargetValue(jsonLoader.toInt(row.get("target_value")));
        entity.setRewardItemId(jsonLoader.toLong(row.get("reward_item_id")));
        entity.setRewardItemType(jsonLoader.toInt(row.get("reward_item_type")));
        entity.setRewardQuantity(jsonLoader.toLong(row.get("reward_quantity")));
        entity.setStatus(jsonLoader.toInt(row.get("status")));
        return entity;
    }
}
