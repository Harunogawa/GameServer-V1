package com.unityonline.gameserver.module.quest.mapper;

import com.unityonline.gameserver.module.quest.entity.PlayerQuestEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerQuestMapper {

    private static final RowMapper<PlayerQuestEntity> ROW_MAPPER = new PlayerQuestRowMapper();
    private final JdbcTemplate jdbcTemplate;

    public PlayerQuestMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 查询指定玩家全部任务状态。
     */
    public List<PlayerQuestEntity> findByPlayerId(Long playerId) {
        String sql = """
                SELECT id, player_id, quest_id, quest_status, progress, reward_claimed, created_at, updated_at
                FROM t_player_quest
                WHERE player_id = ?
                ORDER BY quest_id ASC
                """;
        return jdbcTemplate.query(sql, ROW_MAPPER, playerId);
    }

    /**
     * 查询指定玩家的单个任务状态。
     */
    public Optional<PlayerQuestEntity> findByPlayerIdAndQuestId(Long playerId, Long questId) {
        String sql = """
                SELECT id, player_id, quest_id, quest_status, progress, reward_claimed, created_at, updated_at
                FROM t_player_quest
                WHERE player_id = ? AND quest_id = ?
                LIMIT 1
                """;
        return jdbcTemplate.query(sql, ROW_MAPPER, playerId, questId).stream().findFirst();
    }

    /**
     * 初始化玩家任务状态。
     */
    public int insert(PlayerQuestEntity entity) {
        String sql = """
                INSERT INTO t_player_quest(player_id, quest_id, quest_status, progress, reward_claimed)
                VALUES (?, ?, ?, ?, ?)
                """;
        return jdbcTemplate.update(sql,
                entity.getPlayerId(),
                entity.getQuestId(),
                entity.getQuestStatus(),
                entity.getProgress(),
                entity.getRewardClaimed());
    }

    /**
     * 更新任务进度与完成状态。
     */
    public int updateProgress(Long playerId, Long questId, Integer progress, Integer questStatus) {
        String sql = """
                UPDATE t_player_quest
                SET progress = ?, quest_status = ?
                WHERE player_id = ? AND quest_id = ?
                """;
        return jdbcTemplate.update(sql, progress, questStatus, playerId, questId);
    }

    /**
     * 更新任务奖励领取状态。
     */
    public int updateRewardClaimed(Long playerId, Long questId, Integer rewardClaimed) {
        String sql = "UPDATE t_player_quest SET reward_claimed = ? WHERE player_id = ? AND quest_id = ?";
        return jdbcTemplate.update(sql, rewardClaimed, playerId, questId);
    }

    private static class PlayerQuestRowMapper implements RowMapper<PlayerQuestEntity> {

        /**
         * 将数据库结果映射为玩家任务实体。
         */
        @Override
        public PlayerQuestEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            PlayerQuestEntity entity = new PlayerQuestEntity();
            entity.setId(rs.getLong("id"));
            entity.setPlayerId(rs.getLong("player_id"));
            entity.setQuestId(rs.getLong("quest_id"));
            entity.setQuestStatus(rs.getInt("quest_status"));
            entity.setProgress(rs.getInt("progress"));
            entity.setRewardClaimed(rs.getInt("reward_claimed"));
            entity.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            entity.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return entity;
        }
    }
}
