package com.unityonline.gameserver.module.notice.mapper;

import com.unityonline.gameserver.module.notice.entity.NoticeEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class NoticeMapper {

    private static final RowMapper<NoticeEntity> ROW_MAPPER = new NoticeRowMapper();
    private final JdbcTemplate jdbcTemplate;

    public NoticeMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 查询当前时间范围内生效的公告列表。
     */
    public List<NoticeEntity> findActiveNotices() {
        String sql = """
                SELECT id, notice_id, title, content, priority, status, start_time, end_time, created_at, updated_at
                FROM t_notice
                WHERE status = 1
                  AND (start_time IS NULL OR start_time <= NOW())
                  AND (end_time IS NULL OR end_time >= NOW())
                ORDER BY priority DESC, notice_id DESC
                """;
        return jdbcTemplate.query(sql, ROW_MAPPER);
    }

    private static class NoticeRowMapper implements RowMapper<NoticeEntity> {

        /**
         * 将数据库结果映射为公告实体。
         */
        @Override
        public NoticeEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            NoticeEntity entity = new NoticeEntity();
            entity.setId(rs.getLong("id"));
            entity.setNoticeId(rs.getLong("notice_id"));
            entity.setTitle(rs.getString("title"));
            entity.setContent(rs.getString("content"));
            entity.setPriority(rs.getInt("priority"));
            entity.setStatus(rs.getInt("status"));
            entity.setStartTime(rs.getTimestamp("start_time") == null ? null : rs.getTimestamp("start_time").toLocalDateTime());
            entity.setEndTime(rs.getTimestamp("end_time") == null ? null : rs.getTimestamp("end_time").toLocalDateTime());
            entity.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            entity.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return entity;
        }
    }
}
