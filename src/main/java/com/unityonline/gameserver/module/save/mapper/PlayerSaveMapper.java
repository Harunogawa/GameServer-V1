package com.unityonline.gameserver.module.save.mapper;

import com.unityonline.gameserver.module.save.entity.PlayerSaveEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerSaveMapper {

    private static final RowMapper<PlayerSaveEntity> ROW_MAPPER = new PlayerSaveRowMapper();
    private final JdbcTemplate jdbcTemplate;

    public PlayerSaveMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 根据玩家与槽位查询存档。
     */
    public Optional<PlayerSaveEntity> findByPlayerIdAndSlot(Long playerId, Integer slot) {
        String sql = """
                SELECT id, player_id, save_slot, save_version, save_data_json, created_at, updated_at
                FROM t_player_save
                WHERE player_id = ? AND save_slot = ?
                LIMIT 1
                """;
        List<PlayerSaveEntity> list = jdbcTemplate.query(sql, ROW_MAPPER, playerId, slot);
        return list.stream().findFirst();
    }

    /**
     * 保存或更新存档数据。
     */
    public int upsert(PlayerSaveEntity entity) {
        String sql = """
                INSERT INTO t_player_save(player_id, save_slot, save_version, save_data_json)
                VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    save_version = VALUES(save_version),
                    save_data_json = VALUES(save_data_json)
                """;
        return jdbcTemplate.update(sql, entity.getPlayerId(), entity.getSaveSlot(), entity.getSaveVersion(), entity.getSaveDataJson());
    }

    private static class PlayerSaveRowMapper implements RowMapper<PlayerSaveEntity> {

        /**
         * 将数据库结果映射为存档实体。
         */
        @Override
        public PlayerSaveEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            PlayerSaveEntity entity = new PlayerSaveEntity();
            entity.setId(rs.getLong("id"));
            entity.setPlayerId(rs.getLong("player_id"));
            entity.setSaveSlot(rs.getInt("save_slot"));
            entity.setSaveVersion(rs.getString("save_version"));
            entity.setSaveDataJson(rs.getString("save_data_json"));
            entity.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            entity.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return entity;
        }
    }
}
