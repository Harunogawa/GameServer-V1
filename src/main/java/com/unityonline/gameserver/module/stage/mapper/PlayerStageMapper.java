package com.unityonline.gameserver.module.stage.mapper;

import com.unityonline.gameserver.module.stage.entity.PlayerStageEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerStageMapper {

    private static final RowMapper<PlayerStageEntity> ROW_MAPPER = new PlayerStageRowMapper();
    private final JdbcTemplate jdbcTemplate;

    public PlayerStageMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 查询玩家全部关卡进度。
     */
    public List<PlayerStageEntity> findByPlayerId(Long playerId) {
        String sql = """
                SELECT id, player_id, stage_id, star_count, clear_status, best_score, created_at, updated_at
                FROM t_player_stage
                WHERE player_id = ?
                ORDER BY stage_id ASC
                """;
        return jdbcTemplate.query(sql, ROW_MAPPER, playerId);
    }

    /**
     * 查询玩家单个关卡记录。
     */
    public Optional<PlayerStageEntity> findByPlayerIdAndStageId(Long playerId, Long stageId) {
        String sql = """
                SELECT id, player_id, stage_id, star_count, clear_status, best_score, created_at, updated_at
                FROM t_player_stage
                WHERE player_id = ? AND stage_id = ?
                LIMIT 1
                """;
        return jdbcTemplate.query(sql, ROW_MAPPER, playerId, stageId).stream().findFirst();
    }

    /**
     * 保存或更新关卡结算结果。
     */
    public int upsert(PlayerStageEntity entity) {
        String sql = """
                INSERT INTO t_player_stage(player_id, stage_id, star_count, clear_status, best_score)
                VALUES (?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    star_count = VALUES(star_count),
                    clear_status = VALUES(clear_status),
                    best_score = VALUES(best_score)
                """;
        return jdbcTemplate.update(sql,
                entity.getPlayerId(),
                entity.getStageId(),
                entity.getStarCount(),
                entity.getClearStatus(),
                entity.getBestScore());
    }

    private static class PlayerStageRowMapper implements RowMapper<PlayerStageEntity> {

        /**
         * 将数据库结果映射为玩家关卡实体。
         */
        @Override
        public PlayerStageEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            PlayerStageEntity entity = new PlayerStageEntity();
            entity.setId(rs.getLong("id"));
            entity.setPlayerId(rs.getLong("player_id"));
            entity.setStageId(rs.getLong("stage_id"));
            entity.setStarCount(rs.getInt("star_count"));
            entity.setClearStatus(rs.getInt("clear_status"));
            entity.setBestScore(rs.getLong("best_score"));
            entity.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            entity.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return entity;
        }
    }
}
