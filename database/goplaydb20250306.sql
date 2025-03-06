/*
 Navicat Premium Data Transfer

 Source Server         : TestConnection
 Source Server Type    : MySQL
 Source Server Version : 80037 (8.0.37)
 Source Host           : localhost:3306
 Source Schema         : goplaydb

 Target Server Type    : MySQL
 Target Server Version : 80037 (8.0.37)
 File Encoding         : 65001

 Date: 06/03/2025 22:22:51
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for playlist
-- ----------------------------
DROP TABLE IF EXISTS `playlist`;
CREATE TABLE `playlist`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '用户歌单',
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '',
  `cover_url` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `added_at` timestamp NOT NULL,
  `update_at` timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  `is_active` int NOT NULL DEFAULT 1,
  `is_public` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `id`(`id` ASC) USING BTREE,
  INDEX `fk_playlist_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_playlist_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for playlist_song
-- ----------------------------
DROP TABLE IF EXISTS `playlist_song`;
CREATE TABLE `playlist_song`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `playlist_id` int NULL DEFAULT NULL,
  `song_id` int NOT NULL,
  `added_by` int NOT NULL,
  `added_at` timestamp NOT NULL,
  `added_username` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `is_active` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `playlist_song_ibfk_2`(`song_id` ASC) USING BTREE,
  INDEX `playlist_song_ibfk_1`(`id` ASC) USING BTREE,
  INDEX `playlist_song_ibfk_3`(`added_by` ASC) USING BTREE,
  INDEX `playlist_song_playlist_id`(`playlist_id` ASC) USING BTREE,
  CONSTRAINT `playlist_song_ibfk_2` FOREIGN KEY (`song_id`) REFERENCES `song` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `playlist_song_ibfk_3` FOREIGN KEY (`added_by`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `playlist_song_playlist_id` FOREIGN KEY (`playlist_id`) REFERENCES `playlist` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 57 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for room
-- ----------------------------
DROP TABLE IF EXISTS `room`;
CREATE TABLE `room`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `room_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `owner_id` int NOT NULL,
  `max_users` int NULL DEFAULT 6,
  `current_users` int NULL DEFAULT 0,
  `room_code` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_active` tinyint(1) NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `room_code`(`room_code` ASC) USING BTREE,
  INDEX `owner_id`(`owner_id` ASC) USING BTREE,
  CONSTRAINT `room_ibfk_1` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for room_song
-- ----------------------------
DROP TABLE IF EXISTS `room_song`;
CREATE TABLE `room_song`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `room_id` int NOT NULL,
  `song_id` int NOT NULL,
  `added_by` int NOT NULL,
  `added_at` timestamp NOT NULL,
  `added_username` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `is_active` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `room_song_ibfk_1`(`room_id` ASC) USING BTREE,
  INDEX `room_song_ibfk_2`(`song_id` ASC) USING BTREE,
  INDEX `room_song_ibfk_3`(`added_by` ASC) USING BTREE,
  CONSTRAINT `room_song_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `room_song_ibfk_2` FOREIGN KEY (`song_id`) REFERENCES `song` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `room_song_ibfk_3` FOREIGN KEY (`added_by`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 53 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for room_user
-- ----------------------------
DROP TABLE IF EXISTS `room_user`;
CREATE TABLE `room_user`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `room_id` int NOT NULL,
  `user_id` int NOT NULL,
  `joined_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_active` tinyint(1) NULL DEFAULT 1,
  `privilege` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `room_user_ibfk_1`(`room_id` ASC) USING BTREE,
  INDEX `room_user_ibfk_2`(`user_id` ASC) USING BTREE,
  CONSTRAINT `room_user_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `room_user_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for song
-- ----------------------------
DROP TABLE IF EXISTS `song`;
CREATE TABLE `song`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `is_external` int NOT NULL,
  `file_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `file_path` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `file_duration` int NOT NULL,
  `file_size` int NOT NULL,
  `file_mime_type` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '文件类型',
  `file_cover_path` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '专辑封面path',
  `added_by` int NOT NULL,
  `added_at` timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  `is_active` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `song_ibfk_1`(`added_by` ASC) USING BTREE,
  CONSTRAINT `song_ibfk_1` FOREIGN KEY (`added_by`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 76 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for song_info
-- ----------------------------
DROP TABLE IF EXISTS `song_info`;
CREATE TABLE `song_info`  (
  `id` int NOT NULL,
  `song_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT 'unknown',
  `song_artist` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT 'unknown',
  `song_duration` int NOT NULL,
  `song_album` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `song_size` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  CONSTRAINT `song_info_ibfk_1` FOREIGN KEY (`id`) REFERENCES `song` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `level` int NOT NULL DEFAULT 0 COMMENT '0normal 1manager 2admin',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_song
-- ----------------------------
DROP TABLE IF EXISTS `user_song`;
CREATE TABLE `user_song`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `song_id` int NOT NULL,
  `is_active` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_song_ibfk_1`(`user_id` ASC) USING BTREE,
  INDEX `user_song_ibfk_2`(`song_id` ASC) USING BTREE,
  CONSTRAINT `user_song_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `user_song_ibfk_2` FOREIGN KEY (`song_id`) REFERENCES `song` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_vip
-- ----------------------------
DROP TABLE IF EXISTS `user_vip`;
CREATE TABLE `user_vip`  (
  `id` int NOT NULL,
  `vip_level` tinyint NOT NULL DEFAULT 0 COMMENT 'VIP等级，例如：1-普通VIP，2-高级VIP，3-钻石VIP',
  `start_date` datetime NOT NULL COMMENT 'VIP开始时间',
  `end_date` datetime NOT NULL COMMENT 'VIP结束时间，过期时间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录最后更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `id`(`id` ASC) USING BTREE,
  CONSTRAINT `fk_user_uservip` FOREIGN KEY (`id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
