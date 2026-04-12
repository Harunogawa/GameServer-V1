package com.unityonline.gameserver.module.player.mapper;

import com.unityonline.gameserver.module.player.entity.PlayerEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerMapper {

    private static final RowMapper<PlayerEntity> ROW_MAPPER = new PlayerRowMapper();
    private final JdbcTemplate jdbcTemplate;

    public PlayerMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 根据玩家 ID 查询玩家基础信息。
     */
    public Optional<PlayerEntity> findByPlayerId(Long playerId) {
        String sql = """
                SELECT id, player_id, account_id, nickname, level, exp, gold, diamond, status, created_at, updated_at
                FROM t_player
                WHERE player_id = ?
                LIMIT 1
                """;
        List<PlayerEntity> list = jdbcTemplate.query(sql, ROW_MAPPER, playerId);
        return list.stream().findFirst();
    }

    /**
     * 根据账号 ID 查询玩家基础信息。
     */
    public Optional<PlayerEntity> findByAccountId(Long accountId) {
        String sql = """
                SELECT id, player_id, account_id, nickname, level, exp, gold, diamond, status, created_at, updated_at
                FROM t_player
                WHERE account_id = ?
                LIMIT 1
                """;
        List<PlayerEntity> list = jdbcTemplate.query(sql, ROW_MAPPER, accountId);
        return list.stream().findFirst();
    }

    /**
     * 插入玩家基础数据。
     */
    public int insert(PlayerEntity entity) {
        String sql = """
                INSERT INTO t_player(player_id, account_id, nickname, level, exp, gold, diamond, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        return jdbcTemplate.update(sql,
                entity.getPlayerId(),
                entity.getAccountId(),
                entity.getNickname(),
                entity.getLevel(),
                entity.getExp(),
                entity.getGold(),
                entity.getDiamond(),
                entity.getStatus());
    }

    /**
     * 批量查询玩家信息，用于排行榜昵称展示。
     */
    public List<PlayerEntity> findByPlayerIds(Collection<Long> playerIds) {
        if (playerIds == null || playerIds.isEmpty()) {
            return List.of();
        }
        String placeholders = String.join(",", java.util.Collections.nCopies(playerIds.size(), "?"));
        String sql = """
                SELECT id, player_id, account_id, nickname, level, exp, gold, diamond, status, created_at, updated_at
                FROM t_player
                WHERE player_id IN (%s)
                """.formatted(placeholders);
        return jdbcTemplate.query(sql, ROW_MAPPER, playerIds.toArray());
    }

    private static class PlayerRowMapper implements RowMapper<PlayerEntity> {

        /**
         * 将数据库结果映射为玩家实体。
         */
        @Override
        public PlayerEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            PlayerEntity entity = new PlayerEntity();
            entity.setId(rs.getLong("id"));
            entity.setPlayerId(rs.getLong("player_id"));
            entity.setAccountId(rs.getLong("account_id"));
            entity.setNickname(rs.getString("nickname"));
            entity.setLevel(rs.getInt("level"));
            entity.setExp(rs.getLong("exp"));
            entity.setGold(rs.getLong("gold"));
            entity.setDiamond(rs.getLong("diamond"));
            entity.setStatus(rs.getInt("status"));
            entity.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            entity.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return entity;
        }
    }
}
