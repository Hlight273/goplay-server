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

 Date: 15/03/2025 21:48:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for payment_order
-- ----------------------------
DROP TABLE IF EXISTS `payment_order`;
CREATE TABLE `payment_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '订单编号',
  `prepay_id` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '预支付ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `amount` decimal(10, 2) NOT NULL COMMENT '订单金额',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '订单状态（0:创建, 1:支付中, 2:支付成功, 3:支付失败, 4:已过期）',
  `expire_time` datetime NOT NULL COMMENT '订单过期时间',
  `paid_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '实际支付金额',
  `success_time` datetime NULL DEFAULT NULL COMMENT '支付成功时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no` ASC) USING BTREE,
  UNIQUE INDEX `prepay_id`(`prepay_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_prepay_id`(`prepay_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '支付订单表' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB AUTO_INCREMENT = 86 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for recommend_playlist
-- ----------------------------
DROP TABLE IF EXISTS `recommend_playlist`;
CREATE TABLE `recommend_playlist`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `playlist_id` int NOT NULL,
  `is_active` int NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB AUTO_INCREMENT = 58 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB AUTO_INCREMENT = 107 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for song_comment
-- ----------------------------
DROP TABLE IF EXISTS `song_comment`;
CREATE TABLE `song_comment`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `parent_id` int NULL DEFAULT NULL COMMENT '父评论ID，为NULL表示主评论，否则表示回复',
  `song_id` int NOT NULL,
  `added_by` int NOT NULL,
  `added_at` timestamp NOT NULL,
  `content_text` varchar(648) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '评论内容',
  `like_count` int NOT NULL DEFAULT 0 COMMENT '点赞数',
  `is_active` int NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `song_comment_fk1`(`song_id` ASC) USING BTREE,
  INDEX `song_comment_fk2`(`added_by` ASC) USING BTREE,
  CONSTRAINT `song_comment_fk1` FOREIGN KEY (`song_id`) REFERENCES `song` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `song_comment_fk2` FOREIGN KEY (`added_by`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `level` int NOT NULL DEFAULT 0 COMMENT '0normal 1manager 2admin',
  `nickname` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT 'if null show username',
  `h_points` int NULL DEFAULT 0 COMMENT '用户积分',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
