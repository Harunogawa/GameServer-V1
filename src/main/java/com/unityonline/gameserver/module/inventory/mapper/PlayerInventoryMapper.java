package com.unityonline.gameserver.module.inventory.mapper;

import com.unityonline.gameserver.module.inventory.entity.PlayerInventoryEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerInventoryMapper {

    private static final RowMapper<PlayerInventoryEntity> ROW_MAPPER = new PlayerInventoryRowMapper();
    private final JdbcTemplate jdbcTemplate;

    public PlayerInventoryMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 查询指定玩家的全部背包道具。
     */
    public List<PlayerInventoryEntity> findByPlayerId(Long playerId) {
        String sql = """
                SELECT id, player_id, item_id, item_type, quantity, ext_json, created_at, updated_at
                FROM t_player_inventory
                WHERE player_id = ?
                ORDER BY item_id ASC
                """;
        return jdbcTemplate.query(sql, ROW_MAPPER, playerId);
    }

    /**
     * 查询指定玩家的单个道具。
     */
    public Optional<PlayerInventoryEntity> findByPlayerIdAndItemId(Long playerId, Long itemId) {
        String sql = """
                SELECT id, player_id, item_id, item_type, quantity, ext_json, created_at, updated_at
                FROM t_player_inventory
                WHERE player_id = ? AND item_id = ?
                LIMIT 1
                """;
        List<PlayerInventoryEntity> list = jdbcTemplate.query(sql, ROW_MAPPER, playerId, itemId);
        return list.stream().findFirst();
    }

    /**
     * 保存或更新背包道具数据。
     */
    public int upsert(PlayerInventoryEntity entity) {
        String sql = """
                INSERT INTO t_player_inventory(player_id, item_id, item_type, quantity, ext_json)
                VALUES (?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    item_type = VALUES(item_type),
                    quantity = VALUES(quantity),
                    ext_json = VALUES(ext_json)
                """;
        return jdbcTemplate.update(sql,
                entity.getPlayerId(),
                entity.getItemId(),
                entity.getItemType(),
                entity.getQuantity(),
                entity.getExtJson() == null ? "{}" : entity.getExtJson());
    }

    /**
     * 更新道具数量。
     */
    public int updateQuantity(Long playerId, Long itemId, Long quantity) {
        String sql = "UPDATE t_player_inventory SET quantity = ? WHERE player_id = ? AND item_id = ?";
        return jdbcTemplate.update(sql, quantity, playerId, itemId);
    }

    private static class PlayerInventoryRowMapper implements RowMapper<PlayerInventoryEntity> {

        /**
         * 将数据库结果映射为背包实体。
         */
        @Override
        public PlayerInventoryEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            PlayerInventoryEntity entity = new PlayerInventoryEntity();
            entity.setId(rs.getLong("id"));
            entity.setPlayerId(rs.getLong("player_id"));
            entity.setItemId(rs.getLong("item_id"));
            entity.setItemType(rs.getInt("item_type"));
            entity.setQuantity(rs.getLong("quantity"));
            entity.setExtJson(rs.getString("ext_json"));
            entity.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            entity.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return entity;
        }
    }
}
