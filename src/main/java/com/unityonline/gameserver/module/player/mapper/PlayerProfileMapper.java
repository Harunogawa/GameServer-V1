package com.unityonline.gameserver.module.player.mapper;

import com.unityonline.gameserver.module.player.entity.PlayerProfileEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerProfileMapper {

    private static final RowMapper<PlayerProfileEntity> ROW_MAPPER = new PlayerProfileRowMapper();
    private final JdbcTemplate jdbcTemplate;

    public PlayerProfileMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 根据玩家 ID 查询玩家扩展资料。
     */
    public Optional<PlayerProfileEntity> findByPlayerId(Long playerId) {
        String sql = """
                SELECT id, player_id, account_id, avatar_url, last_login_time, last_logout_time, created_at, updated_at
                FROM t_player_profile
                WHERE player_id = ?
                LIMIT 1
                """;
        List<PlayerProfileEntity> list = jdbcTemplate.query(sql, ROW_MAPPER, playerId);
        return list.stream().findFirst();
    }

    /**
     * 插入玩家扩展资料。
     */
    public int insert(PlayerProfileEntity entity) {
        String sql = """
                INSERT INTO t_player_profile(player_id, account_id, avatar_url, last_login_time, last_logout_time)
                VALUES (?, ?, ?, ?, ?)
                """;
        return jdbcTemplate.update(sql,
                entity.getPlayerId(),
                entity.getAccountId(),
                entity.getAvatarUrl(),
                entity.getLastLoginTime(),
                entity.getLastLogoutTime());
    }

    /**
     * 更新最近登录时间。
     */
    public int updateLastLoginTime(Long playerId, LocalDateTime loginTime) {
        String sql = "UPDATE t_player_profile SET last_login_time = ? WHERE player_id = ?";
        return jdbcTemplate.update(sql, loginTime, playerId);
    }

    /**
     * 更新最近登出时间。
     */
    public int updateLastLogoutTime(Long playerId, LocalDateTime logoutTime) {
        String sql = "UPDATE t_player_profile SET last_logout_time = ? WHERE player_id = ?";
        return jdbcTemplate.update(sql, logoutTime, playerId);
    }

    private static class PlayerProfileRowMapper implements RowMapper<PlayerProfileEntity> {

        /**
         * 将数据库结果映射为玩家扩展资料实体。
         */
        @Override
        public PlayerProfileEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            PlayerProfileEntity entity = new PlayerProfileEntity();
            entity.setId(rs.getLong("id"));
            entity.setPlayerId(rs.getLong("player_id"));
            entity.setAccountId(rs.getLong("account_id"));
            entity.setAvatarUrl(rs.getString("avatar_url"));
            entity.setLastLoginTime(rs.getTimestamp("last_login_time") == null ? null : rs.getTimestamp("last_login_time").toLocalDateTime());
            entity.setLastLogoutTime(rs.getTimestamp("last_logout_time") == null ? null : rs.getTimestamp("last_logout_time").toLocalDateTime());
            entity.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            entity.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return entity;
        }
    }
}
