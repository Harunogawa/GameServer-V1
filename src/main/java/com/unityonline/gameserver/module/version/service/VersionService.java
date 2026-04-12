package com.unityonline.gameserver.module.version.service;

import com.unityonline.gameserver.common.exception.BizException;
import com.unityonline.gameserver.module.version.entity.VersionConfigEntity;
import com.unityonline.gameserver.module.version.mapper.VersionConfigMapper;
import org.springframework.stereotype.Service;

@Service
public class VersionService {

    private final VersionConfigMapper versionConfigMapper;

    public VersionService(VersionConfigMapper versionConfigMapper) {
        this.versionConfigMapper = versionConfigMapper;
    }

    /**
     * 获取指定平台的客户端版本配置。
     */
    public VersionConfigEntity getVersionInfo(String platform) {
        return versionConfigMapper.findLatestByPlatform(normalizePlatform(platform))
                .orElseThrow(() -> new BizException(4501, "version config not found"));
    }

    /**
     * 获取指定平台的资源版本配置。
     */
    public VersionConfigEntity getResourceVersion(String platform) {
        return getVersionInfo(platform);
    }

    /**
     * 规整平台字段，避免空值查询失败。
     */
    private String normalizePlatform(String platform) {
        return (platform == null || platform.isBlank()) ? "all" : platform.trim();
    }
}
