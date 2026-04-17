package com.unityonline.gameserver.module.version.mapper;

import com.unityonline.gameserver.module.version.entity.VersionConfigEntity;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class VersionConfigMapper {

    private final VersionConfigJsonRepository repository;

    public VersionConfigMapper(VersionConfigJsonRepository repository) {
        this.repository = repository;
    }

    public Optional<VersionConfigEntity> findLatestByPlatform(String platform) {
        return repository.findLatestByPlatform(platform);
    }
}
