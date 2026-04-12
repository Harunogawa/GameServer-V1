package com.unityonline.gameserver.module.auth.mapper;

import com.unityonline.gameserver.module.auth.entity.AuthAccountEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class AuthAccountMapper {

    private static final RowMapper<AuthAccountEntity> ROW_MAPPER = new AuthAccountRowMapper();
    private final JdbcTemplate jdbcTemplate;

    public AuthAccountMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 根据用户名查询账号。
     */
    public Optional<AuthAccountEntity> findByUsername(String username) {
        String sql = """
                SELECT id, account_id, player_id, username, password_hash, salt, last_login_time, last_login_ip, status, created_at, updated_at
                FROM t_auth_account
                WHERE username = ?
                LIMIT 1
                """;
        List<AuthAccountEntity> list = jdbcTemplate.query(sql, ROW_MAPPER, username);
        return list.stream().findFirst();
    }

    /**
     * 根据账号 ID 查询账号。
     */
    public Optional<AuthAccountEntity> findByAccountId(Long accountId) {
        String sql = """
                SELECT id, account_id, player_id, username, password_hash, salt, last_login_time, last_login_ip, status, created_at, updated_at
                FROM t_auth_account
                WHERE account_id = ?
                LIMIT 1
                """;
        List<AuthAccountEntity> list = jdbcTemplate.query(sql, ROW_MAPPER, accountId);
        return list.stream().findFirst();
    }

    /**
     * 插入账号数据。
     */
    public int insert(AuthAccountEntity entity) {
        String sql = """
                INSERT INTO t_auth_account(account_id, player_id, username, password_hash, salt, last_login_time, last_login_ip, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        return jdbcTemplate.update(sql,
                entity.getAccountId(),
                entity.getPlayerId(),
                entity.getUsername(),
                entity.getPasswordHash(),
                entity.getSalt(),
                entity.getLastLoginTime(),
                entity.getLastLoginIp(),
                entity.getStatus());
    }

    /**
     * 更新最近登录信息。
     */
    public int updateLoginInfo(Long accountId, LocalDateTime loginTime, String loginIp) {
        String sql = """
                UPDATE t_auth_account
                SET last_login_time = ?, last_login_ip = ?
                WHERE account_id = ?
                """;
        return jdbcTemplate.update(sql, loginTime, loginIp, accountId);
    }

    /**
     * 回填账号对应的玩家 ID。
     */
    public int updatePlayerId(Long accountId, Long playerId) {
        String sql = "UPDATE t_auth_account SET player_id = ? WHERE account_id = ?";
        return jdbcTemplate.update(sql, playerId, accountId);
    }

    private static class AuthAccountRowMapper implements RowMapper<AuthAccountEntity> {

        /**
         * 将数据库结果映射为账号实体。
         */
        @Override
        public AuthAccountEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            AuthAccountEntity entity = new AuthAccountEntity();
            entity.setId(rs.getLong("id"));
            entity.setAccountId(rs.getLong("account_id"));
            entity.setPlayerId(rs.getLong("player_id"));
            entity.setUsername(rs.getString("username"));
            entity.setPasswordHash(rs.getString("password_hash"));
            entity.setSalt(rs.getString("salt"));
            entity.setLastLoginTime(rs.getTimestamp("last_login_time") == null ? null : rs.getTimestamp("last_login_time").toLocalDateTime());
            entity.setLastLoginIp(rs.getString("last_login_ip"));
            entity.setStatus(rs.getInt("status"));
            entity.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            entity.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return entity;
        }
    }
}
