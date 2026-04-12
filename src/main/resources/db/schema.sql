CREATE DATABASE IF NOT EXISTS game_server DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE game_server;

CREATE TABLE IF NOT EXISTS t_player (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    player_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    nickname VARCHAR(64) NOT NULL,
    level INT NOT NULL DEFAULT 1,
    exp BIGINT NOT NULL DEFAULT 0,
    gold BIGINT NOT NULL DEFAULT 0,
    diamond BIGINT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_player_id (player_id),
    KEY idx_account_id (account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_auth_account (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT NOT NULL,
    player_id BIGINT NOT NULL,
    username VARCHAR(64) NOT NULL,
    password_hash VARCHAR(256) NOT NULL,
    salt VARCHAR(128) NOT NULL,
    last_login_time DATETIME NULL,
    last_login_ip VARCHAR(64) NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_account_id (account_id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_player_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    player_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    avatar_url VARCHAR(255) NULL,
    last_login_time DATETIME NULL,
    last_logout_time DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_player_profile_player (player_id),
    UNIQUE KEY uk_player_profile_account (account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_player_save (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    player_id BIGINT NOT NULL,
    save_slot INT NOT NULL DEFAULT 1,
    save_version VARCHAR(32) NOT NULL,
    save_data_json JSON NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_player_slot (player_id, save_slot)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_player_inventory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    player_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    item_type INT NOT NULL DEFAULT 0,
    quantity BIGINT NOT NULL DEFAULT 0,
    ext_json JSON NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_player_item (player_id, item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_player_quest (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    player_id BIGINT NOT NULL,
    quest_id BIGINT NOT NULL,
    quest_status TINYINT NOT NULL DEFAULT 0,
    progress INT NOT NULL DEFAULT 0,
    reward_claimed TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_player_quest (player_id, quest_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_quest_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    quest_id BIGINT NOT NULL,
    quest_name VARCHAR(128) NOT NULL,
    quest_desc VARCHAR(255) NOT NULL,
    quest_type INT NOT NULL,
    target_value INT NOT NULL DEFAULT 1,
    reward_item_id BIGINT NOT NULL DEFAULT 0,
    reward_item_type INT NOT NULL DEFAULT 0,
    reward_quantity BIGINT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_quest_id (quest_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_player_stage (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    player_id BIGINT NOT NULL,
    stage_id BIGINT NOT NULL,
    star_count INT NOT NULL DEFAULT 0,
    clear_status TINYINT NOT NULL DEFAULT 0,
    best_score BIGINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_player_stage (player_id, stage_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_stage_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    stage_id BIGINT NOT NULL,
    stage_name VARCHAR(128) NOT NULL,
    reward_item_id BIGINT NOT NULL DEFAULT 0,
    reward_item_type INT NOT NULL DEFAULT 0,
    reward_quantity BIGINT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_stage_config (stage_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_notice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    notice_id BIGINT NOT NULL,
    title VARCHAR(128) NOT NULL,
    content TEXT NOT NULL,
    priority INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    start_time DATETIME NULL,
    end_time DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_notice_id (notice_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_version_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    version_code INT NOT NULL,
    version_name VARCHAR(32) NOT NULL,
    platform VARCHAR(32) NOT NULL DEFAULT 'all',
    min_client_version VARCHAR(32) NOT NULL,
    latest_client_version VARCHAR(32) NOT NULL,
    resource_version VARCHAR(64) NOT NULL DEFAULT '1.0.0',
    resource_url VARCHAR(255) NOT NULL DEFAULT '',
    download_url VARCHAR(255) NOT NULL DEFAULT '',
    force_update TINYINT NOT NULL DEFAULT 0,
    notice TEXT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_version_code_platform (version_code, platform)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_rank_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rank_type INT NOT NULL,
    player_id BIGINT NOT NULL,
    score BIGINT NOT NULL DEFAULT 0,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_rank_record (rank_type, player_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_rank_snapshot (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rank_type INT NOT NULL,
    player_id BIGINT NOT NULL,
    score BIGINT NOT NULL DEFAULT 0,
    rank_no INT NOT NULL DEFAULT 0,
    snapshot_date DATE NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_rank_snapshot (rank_type, player_id, snapshot_date),
    KEY idx_rank_date (rank_type, snapshot_date, rank_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO t_quest_config(quest_id, quest_name, quest_desc, quest_type, target_value, reward_item_id, reward_item_type, reward_quantity, status)
VALUES
    (1001, '首次通关', '完成任意关卡 1 次', 1, 1, 2001, 1, 100, 1),
    (1002, '累计星级', '累计获得 6 星', 2, 6, 2002, 1, 50, 1)
ON DUPLICATE KEY UPDATE
    quest_name = VALUES(quest_name),
    quest_desc = VALUES(quest_desc),
    quest_type = VALUES(quest_type),
    target_value = VALUES(target_value),
    reward_item_id = VALUES(reward_item_id),
    reward_item_type = VALUES(reward_item_type),
    reward_quantity = VALUES(reward_quantity),
    status = VALUES(status);

INSERT INTO t_stage_config(stage_id, stage_name, reward_item_id, reward_item_type, reward_quantity, status)
VALUES
    (10001, '第 1-1 关', 3001, 1, 10, 1),
    (10002, '第 1-2 关', 3001, 1, 15, 1)
ON DUPLICATE KEY UPDATE
    stage_name = VALUES(stage_name),
    reward_item_id = VALUES(reward_item_id),
    reward_item_type = VALUES(reward_item_type),
    reward_quantity = VALUES(reward_quantity),
    status = VALUES(status);

INSERT INTO t_notice(notice_id, title, content, priority, status, start_time, end_time)
VALUES
    (1, '欢迎公告', '欢迎进入 GameServer 示例项目。', 100, 1, NOW(), DATE_ADD(NOW(), INTERVAL 365 DAY))
ON DUPLICATE KEY UPDATE
    title = VALUES(title),
    content = VALUES(content),
    priority = VALUES(priority),
    status = VALUES(status),
    start_time = VALUES(start_time),
    end_time = VALUES(end_time);

INSERT INTO t_version_config(version_code, version_name, platform, min_client_version, latest_client_version, resource_version, resource_url, download_url, force_update, notice, status)
VALUES
    (1, '1.0.0', 'all', '1.0.0', '1.0.0', 'res-1.0.0', 'https://example.com/res-1.0.0.zip', 'https://example.com/client-1.0.0.apk', 0, '当前已是最新版本。', 1)
ON DUPLICATE KEY UPDATE
    version_name = VALUES(version_name),
    min_client_version = VALUES(min_client_version),
    latest_client_version = VALUES(latest_client_version),
    resource_version = VALUES(resource_version),
    resource_url = VALUES(resource_url),
    download_url = VALUES(download_url),
    force_update = VALUES(force_update),
    notice = VALUES(notice),
    status = VALUES(status);
