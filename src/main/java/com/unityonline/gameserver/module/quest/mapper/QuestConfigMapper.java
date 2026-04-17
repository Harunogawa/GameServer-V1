package com.unityonline.gameserver.module.quest.mapper;

import com.unityonline.gameserver.module.quest.entity.QuestConfigEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class QuestConfigMapper {

    private final QuestConfigJsonRepository repository;

    public QuestConfigMapper(QuestConfigJsonRepository repository) {
        this.repository = repository;
    }

    public List<QuestConfigEntity> findActiveList() {
        return repository.findAll().stream()
                .filter(config -> config.getStatus() != null && config.getStatus() == 1)
                .toList();
    }

    public Optional<QuestConfigEntity> findByQuestId(Long questId) {
        return repository.findByQuestId(questId)
                .filter(config -> config.getStatus() != null && config.getStatus() == 1);
    }
}
