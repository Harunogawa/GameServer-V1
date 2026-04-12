package com.unityonline.gameserver.module.rank.mapper;

import com.unityonline.gameserver.module.rank.entity.RankRecordEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class RankRecordMapper {

    private static final RowMapper<RankRecordEntity> ROW_MAPPER = new RankRecordRowMapper();
    private final JdbcTemplate jdbcTemplate;

    public RankRecordMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 查询指定排行榜类型下的单个玩家分数。
     */
    public Optional<RankRecordEntity> findByRankTypeAndPlayerId(Integer rankType, Long playerId) {
        String sql = """
                SELECT id, rank_type, player_id, score, updated_at, created_at
                FROM t_rank_record
                WHERE rank_type = ? AND player_id = ?
                LIMIT 1
                """;
        return jdbcTemplate.query(sql, ROW_MAPPER, rankType, playerId).stream().findFirst();
    }

    /**
     * 查询指定排行榜类型下的全部榜单记录。
     */
    public List<RankRecordEntity> findByRankType(Integer rankType) {
        String sql = """
                SELECT id, rank_type, player_id, score, updated_at, created_at
                FROM t_rank_record
                WHERE rank_type = ?
                ORDER BY score DESC, player_id ASC
                """;
        return jdbcTemplate.query(sql, ROW_MAPPER, rankType);
    }

    /**
     * 保存或更新排行榜分数。
     */
    public int upsert(Integer rankType, Long playerId, Long score) {
        String sql = """
                INSERT INTO t_rank_record(rank_type, player_id, score)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    score = GREATEST(score, VALUES(score))
                """;
        return jdbcTemplate.update(sql, rankType, playerId, score);
    }

    /**
     * 根据分数和玩家 ID 计算当前名次，分数高者优先，分数相同则 playerId 小者优先。
     */
    public int calculateRankNo(Integer rankType, Long playerId, Long score) {
        String sql = """
                SELECT COUNT(1)
                FROM t_rank_record
                WHERE rank_type = ?
                  AND (score > ? OR (score = ? AND player_id < ?))
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, rankType, score, score, playerId);
        return (count == null ? 0 : count) + 1;
    }

    private static class RankRecordRowMapper implements RowMapper<RankRecordEntity> {

        /**
         * 将数据库结果映射为排行榜记录实体。
         */
        @Override
        public RankRecordEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            RankRecordEntity entity = new RankRecordEntity();
            entity.setId(rs.getLong("id"));
            entity.setRankType(rs.getInt("rank_type"));
            entity.setPlayerId(rs.getLong("player_id"));
            entity.setScore(rs.getLong("score"));
            entity.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            entity.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return entity;
        }
    }
}
