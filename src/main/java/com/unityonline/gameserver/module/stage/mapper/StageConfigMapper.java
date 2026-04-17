package com.unityonline.gameserver.module.stage.mapper;

import com.unityonline.gameserver.module.stage.entity.StageConfigEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class StageConfigMapper {

    private final StageConfigJsonRepository repository;

    public StageConfigMapper(StageConfigJsonRepository repository) {
        this.repository = repository;
    }

    public List<StageConfigEntity> findActiveList() {
        return repository.findAll().stream()
                .filter(config -> config.getStatus() != null && config.getStatus() == 1)
                .toList();
    }

    public Optional<StageConfigEntity> findByStageId(Long stageId) {
        return repository.findByStageId(stageId)
                .filter(config -> config.getStatus() != null && config.getStatus() == 1);
    }
}
