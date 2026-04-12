package com.unityonline.gameserver.module.quest.mapper;

import com.unityonline.gameserver.module.quest.entity.QuestConfigEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class QuestConfigMapper {

    private static final RowMapper<QuestConfigEntity> ROW_MAPPER = new QuestConfigRowMapper();
    private final JdbcTemplate jdbcTemplate;

    public QuestConfigMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 查询全部启用中的任务配置。
     */
    public List<QuestConfigEntity> findActiveList() {
        String sql = """
                SELECT id, quest_id, quest_name, quest_desc, quest_type, target_value, reward_item_id, reward_item_type, reward_quantity, status, created_at, updated_at
                FROM t_quest_config
                WHERE status = 1
                ORDER BY quest_id ASC
                """;
        return jdbcTemplate.query(sql, ROW_MAPPER);
    }

    /**
     * 根据 questId 查询任务配置。
     */
    public Optional<QuestConfigEntity> findByQuestId(Long questId) {
        String sql = """
                SELECT id, quest_id, quest_name, quest_desc, quest_type, target_value, reward_item_id, reward_item_type, reward_quantity, status, created_at, updated_at
                FROM t_quest_config
                WHERE quest_id = ? AND status = 1
                LIMIT 1
                """;
        return jdbcTemplate.query(sql, ROW_MAPPER, questId).stream().findFirst();
    }

    private static class QuestConfigRowMapper implements RowMapper<QuestConfigEntity> {

        /**
         * 将数据库结果映射为任务配置实体。
         */
        @Override
        public QuestConfigEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            QuestConfigEntity entity = new QuestConfigEntity();
            entity.setId(rs.getLong("id"));
            entity.setQuestId(rs.getLong("quest_id"));
            entity.setQuestName(rs.getString("quest_name"));
            entity.setQuestDesc(rs.getString("quest_desc"));
            entity.setQuestType(rs.getInt("quest_type"));
            entity.setTargetValue(rs.getInt("target_value"));
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
