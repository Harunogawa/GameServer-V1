package com.unityonline.gameserver.module.stage.mapper;

import com.unityonline.gameserver.common.config.GameConfigJsonLoader;
import com.unityonline.gameserver.module.stage.entity.StageConfigEntity;
import jakarta.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Repository;

@Repository
public class StageConfigJsonRepository {

    private static final String CONFIG_PATH = "classpath:config/tbstageconfig.json";

    private final GameConfigJsonLoader jsonLoader;
    private final AtomicReference<List<StageConfigEntity>> cache = new AtomicReference<>(List.of());

    public StageConfigJsonRepository(GameConfigJsonLoader jsonLoader) {
        this.jsonLoader = jsonLoader;
    }

    @PostConstruct
    public void load() {
        List<StageConfigEntity> configs = jsonLoader.loadList(CONFIG_PATH).stream()
                .map(this::toEntity)
                .sorted(Comparator.comparing(StageConfigEntity::getStageId))
                .toList();
        cache.set(configs);
    }

    public List<StageConfigEntity> findAll() {
        return cache.get();
    }

    public Optional<StageConfigEntity> findByStageId(Long stageId) {
        return cache.get().stream()
                .filter(config -> config.getStageId().equals(stageId))
                .findFirst();
    }

    private StageConfigEntity toEntity(Map<String, Object> row) {
        StageConfigEntity entity = new StageConfigEntity();
        entity.setStageId(jsonLoader.toLong(row.get("stage_id")));
        entity.setStageName(jsonLoader.toText(row.get("stage_name")));
        entity.setRewardItemId(jsonLoader.toLong(row.get("reward_item_id")));
        entity.setRewardItemType(jsonLoader.toInt(row.get("reward_item_type")));
        entity.setRewardQuantity(jsonLoader.toLong(row.get("reward_quantity")));
        entity.setStatus(jsonLoader.toInt(row.get("status")));
        return entity;
    }
}
