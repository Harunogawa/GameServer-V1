package com.unityonline.gameserver.module.version.mapper;

import com.unityonline.gameserver.common.config.GameConfigJsonLoader;
import com.unityonline.gameserver.module.version.entity.VersionConfigEntity;
import jakarta.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Repository;

@Repository
public class VersionConfigJsonRepository {

    private static final String CONFIG_PATH = "classpath:config/tbversionconfig.json";

    private final GameConfigJsonLoader jsonLoader;
    private final AtomicReference<List<VersionConfigEntity>> cache = new AtomicReference<>(List.of());

    public VersionConfigJsonRepository(GameConfigJsonLoader jsonLoader) {
        this.jsonLoader = jsonLoader;
    }

    @PostConstruct
    public void load() {
        List<VersionConfigEntity> configs = jsonLoader.loadList(CONFIG_PATH).stream()
                .map(this::toEntity)
                .sorted(Comparator.comparing(VersionConfigEntity::getVersionCode).reversed())
                .toList();
        cache.set(configs);
    }

    public Optional<VersionConfigEntity> findLatestByPlatform(String platform) {
        return cache.get().stream()
                .filter(config -> config.getStatus() != null && config.getStatus() == 1)
                .filter(config -> platform.equals(config.getPlatform()) || "all".equals(config.getPlatform()))
                .findFirst();
    }

    private VersionConfigEntity toEntity(Map<String, Object> row) {
        VersionConfigEntity entity = new VersionConfigEntity();
        entity.setVersionCode(jsonLoader.toInt(row.get("version_code")));
        entity.setVersionName(jsonLoader.toText(row.get("version_name")));
        entity.setPlatform(jsonLoader.toText(row.get("platform")));
        entity.setMinClientVersion(jsonLoader.toText(row.get("min_client_version")));
        entity.setLatestClientVersion(jsonLoader.toText(row.get("latest_client_version")));
        entity.setResourceVersion(jsonLoader.toText(row.get("resource_version")));
        entity.setResourceUrl(jsonLoader.toText(row.get("resource_url")));
        entity.setDownloadUrl(jsonLoader.toText(row.get("download_url")));
        entity.setForceUpdate(jsonLoader.toInt(row.get("force_update")));
        entity.setNotice(jsonLoader.toText(row.get("notice")));
        entity.setStatus(jsonLoader.toInt(row.get("status")));
        return entity;
    }
}
