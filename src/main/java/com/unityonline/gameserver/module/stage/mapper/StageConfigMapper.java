package com.unityonline.gameserver.module.stage.mapper;

import com.unityonline.gameserver.module.stage.entity.StageConfigEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class StageConfigMapper {

    private static final RowMapper<StageConfigEntity> ROW_MAPPER = new StageConfigRowMapper();
    private final JdbcTemplate jdbcTemplate;

    public StageConfigMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 查询全部启用中的关卡配置。
     */
    public List<StageConfigEntity> findActiveList() {
        String sql = """
                SELECT id, stage_id, stage_name, reward_item_id, reward_item_type, reward_quantity, status, created_at, updated_at
                FROM t_stage_config
                WHERE status = 1
                ORDER BY stage_id ASC
                """;
        return jdbcTemplate.query(sql, ROW_MAPPER);
    }

    /**
     * 根据关卡 ID 查询配置。
     */
    public Optional<StageConfigEntity> findByStageId(Long stageId) {
        String sql = """
                SELECT id, stage_id, stage_name, reward_item_id, reward_item_type, reward_quantity, status, created_at, updated_at
                FROM t_stage_config
                WHERE stage_id = ? AND status = 1
                LIMIT 1
                """;
        return jdbcTemplate.query(sql, ROW_MAPPER, stageId).stream().findFirst();
    }

    private static class StageConfigRowMapper implements RowMapper<StageConfigEntity> {

        /**
         * 将数据库结果映射为关卡配置实体。
         */
        @Override
        public StageConfigEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            StageConfigEntity entity = new StageConfigEntity();
            entity.setId(rs.getLong("id"));
            entity.setStageId(rs.getLong("stage_id"));
            entity.setStageName(rs.getString("stage_name"));
            entity.setRewardItemId(rs.getLong("reward_item_id"));
            entity.setRewardItemType(rs.getInt("reward_item_type"));
            entity.setRewardQuantity(rs.getLong("reward_quantity"));
            entity.setStatus(rs.getInt("status"));
            entity.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            entity.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return entity;
        }
    }
}
