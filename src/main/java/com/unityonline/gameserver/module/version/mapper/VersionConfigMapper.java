package com.unityonline.gameserver.module.version.mapper;

import com.unityonline.gameserver.module.version.entity.VersionConfigEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class VersionConfigMapper {

    private static final RowMapper<VersionConfigEntity> ROW_MAPPER = new VersionConfigRowMapper();
    private final JdbcTemplate jdbcTemplate;

    public VersionConfigMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 按平台查询当前启用的最新版本配置。
     */
    public Optional<VersionConfigEntity> findLatestByPlatform(String platform) {
        String sql = """
                SELECT id, version_code, version_name, platform, min_client_version, latest_client_version, resource_version, resource_url, download_url, force_update, notice, status, created_at, updated_at
                FROM t_version_config
                WHERE status = 1 AND (platform = ? OR platform = 'all')
                ORDER BY version_code DESC
                LIMIT 1
                """;
        List<VersionConfigEntity> list = jdbcTemplate.query(sql, ROW_MAPPER, platform);
        return list.stream().findFirst();
    }

    private static class VersionConfigRowMapper implements RowMapper<VersionConfigEntity> {

        /**
         * 将数据库结果映射为版本配置实体。
         */
        @Override
        public VersionConfigEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            VersionConfigEntity entity = new VersionConfigEntity();
            entity.setId(rs.getLong("id"));
            entity.setVersionCode(rs.getInt("version_code"));
            entity.setVersionName(rs.getString("version_name"));
            entity.setPlatform(rs.getString("platform"));
            entity.setMinClientVersion(rs.getString("min_client_version"));
            entity.setLatestClientVersion(rs.getString("latest_client_version"));
            entity.setResourceVersion(rs.getString("resource_version"));
            entity.setResourceUrl(rs.getString("resource_url"));
            entity.setDownloadUrl(rs.getString("download_url"));
            entity.setForceUpdate(rs.getInt("force_update"));
            entity.setNotice(rs.getString("notice"));
            entity.setStatus(rs.getInt("status"));
            entity.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            entity.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return entity;
        }
    }
}
