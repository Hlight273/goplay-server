SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 创建数据库并设置字符集
CREATE DATABASE IF NOT EXISTS goplaydb CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci;
USE goplaydb;

-- ----------------------------
-- 表结构 for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- 表结构 for room
-- ----------------------------
DROP TABLE IF EXISTS `room`;
CREATE TABLE `room` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `room_name` VARCHAR(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
    `owner_id` INT NOT NULL,           -- 房主的用户 ID
    `max_users` INT DEFAULT 6,        -- 最大用户数
    `current_users` INT DEFAULT 0,    -- 当前在线用户数
    `room_code` VARCHAR(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci UNIQUE NOT NULL, -- 房间代码
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 创建时间
    `is_active` BOOLEAN DEFAULT TRUE,  -- 房间状态，是否仍然有效
    FOREIGN KEY (`owner_id`) REFERENCES `user`(`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci;

-- ----------------------------
-- 表结构 for song
-- ----------------------------
DROP TABLE IF EXISTS `song`;
CREATE TABLE `song` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `room_id` INT NOT NULL,
    `song_url` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
    `added_by` INT NOT NULL,
    FOREIGN KEY (`room_id`) REFERENCES `room`(`id`),
    FOREIGN KEY (`added_by`) REFERENCES `user`(`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci;

DROP TABLE IF EXISTS `room_user`;
CREATE TABLE room_user (
    room_id INT NOT NULL,
    user_id INT NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (room_id) REFERENCES room(id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    PRIMARY KEY (room_id, user_id)
);

SET FOREIGN_KEY_CHECKS = 1;