/*
 Navicat Premium Data Transfer

 Source Server         : MySQL
 Source Server Type    : MySQL
 Source Server Version : 50635
 Source Host           : localhost:3306
 Source Schema         : super

 Target Server Type    : MySQL
 Target Server Version : 50635
 File Encoding         : 65001

 Date: 23/06/2023 13:42:14
*/
CREATE DATABASE IF NOT EXISTS campus_partner;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 博客表
-- ----------------------------
DROP TABLE IF EXISTS `blog`;
CREATE TABLE `blog`
(
    `id`           bigint(20) UNSIGNED                                            NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`      bigint(20) UNSIGNED                                            NOT NULL COMMENT '用户id',
    `title`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '标题',
    `images`       varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '图片，最多9张，多张以\",\"隔开',
    `content`      varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文章',
    `liked_num`    int(8) UNSIGNED                                                NULL     DEFAULT 0 COMMENT '点赞数量',
    `comments_num` int(8) UNSIGNED                                                NULL     DEFAULT 0 COMMENT '评论数量',
    `create_time`  timestamp                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  timestamp                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 19
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = COMPACT;

-- ----------------------------
-- 博客评论表
-- ----------------------------
DROP TABLE IF EXISTS `blog_comments`;
CREATE TABLE `blog_comments`
(
    `id`          bigint(20) UNSIGNED                                           NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`     bigint(20) UNSIGNED                                           NOT NULL COMMENT '用户id',
    `blog_id`     bigint(20) UNSIGNED                                           NOT NULL COMMENT '博文id',
    `parent_id`   bigint(20) UNSIGNED                                           NULL     DEFAULT NULL COMMENT '关联的1级评论id，如果是一级评论，则值为0',
    `answer_id`   bigint(20) UNSIGNED                                           NULL     DEFAULT NULL COMMENT '回复的评论id',
    `content`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '回复的内容',
    `liked_num`   int(8) UNSIGNED                                               NULL     DEFAULT 0 COMMENT '点赞数',
    `status`      tinyint(1) UNSIGNED                                           NULL     DEFAULT NULL COMMENT '状态，0：正常，1：被举报，2：禁止查看',
    `create_time` timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 6
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = COMPACT;

-- ----------------------------
-- 博客点赞表
-- ----------------------------
DROP TABLE IF EXISTS `blog_like`;
CREATE TABLE `blog_like`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `blog_id`     bigint(20) NOT NULL COMMENT '博文id',
    `user_id`     bigint(20) NOT NULL COMMENT '用户id',
    `create_time` datetime   NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime   NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`   tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = COMPACT;

-- ----------------------------
-- 聊天信息表
-- ----------------------------
DROP TABLE IF EXISTS `chat`;
CREATE TABLE `chat`
(
    `id`          bigint(20)                                                    NOT NULL AUTO_INCREMENT COMMENT '聊天记录id',
    `from_id`     bigint(20)                                                    NOT NULL COMMENT '发送消息id',
    `to_id`       bigint(20)                                                    NULL DEFAULT NULL COMMENT '接收消息id',
    `text`        varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    `chat_type`   tinyint(4)                                                    NOT NULL COMMENT '聊天类型 1-私聊 2-群聊',
    `create_time` datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP,
    `team_id`     bigint(20)                                                    NULL DEFAULT NULL,
    `is_delete`   tinyint(4)                                                    NULL DEFAULT 0,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 37
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '聊天消息表'
  ROW_FORMAT = Compact;

-- ----------------------------
-- 评论点赞表
-- ----------------------------
DROP TABLE IF EXISTS `comment_like`;
CREATE TABLE `comment_like`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `comment_id`  bigint(20) NOT NULL COMMENT '评论id',
    `user_id`     bigint(20) NOT NULL COMMENT '用户id',
    `create_time` datetime   NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime   NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`   tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 10
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = COMPACT;

-- ----------------------------
-- 用户关注表
-- ----------------------------
DROP TABLE IF EXISTS `follow`;
CREATE TABLE `follow`
(
    `id`             bigint(20)          NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`        bigint(20) UNSIGNED NOT NULL COMMENT '用户id',
    `follow_user_id` bigint(20) UNSIGNED NOT NULL COMMENT '关注的用户id',
    `create_time`    timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`      tinyint(4)          NULL     DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 44
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = COMPACT;

-- ----------------------------
-- 好友申请表
-- ----------------------------
DROP TABLE IF EXISTS `friends`;
CREATE TABLE `friends`
(
    `id`          bigint(20)                                                    NOT NULL AUTO_INCREMENT COMMENT '好友申请id',
    `from_id`     bigint(20)                                                    NOT NULL COMMENT '发送申请的用户id',
    `receive_id`  bigint(20)                                                    NULL     DEFAULT NULL COMMENT '接收申请的用户id ',
    `is_read`     tinyint(4)                                                    NOT NULL DEFAULT 0 COMMENT '是否已读(0-未读 1-已读)',
    `status`      tinyint(4)                                                    NOT NULL DEFAULT 0 COMMENT '申请状态 默认0 （0-未通过 1-已同意 2-已过期 3-不同意）',
    `create_time` datetime                                                      NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime                                                      NULL     DEFAULT CURRENT_TIMESTAMP,
    `is_delete`   tinyint(4)                                                    NOT NULL DEFAULT 0 COMMENT '是否删除',
    `remark`      varchar(214) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '好友申请备注信息',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '好友申请管理表'
  ROW_FORMAT = Compact;

-- ----------------------------
-- 消息表
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`
(
    `id`          bigint(20)                                                    NOT NULL AUTO_INCREMENT COMMENT '主键',
    `type`        tinyint(4)                                                    NULL DEFAULT NULL COMMENT '类型-1 点赞',
    `from_id`     bigint(20)                                                    NULL DEFAULT NULL COMMENT '消息发送的用户id',
    `to_id`       bigint(20)                                                    NULL DEFAULT NULL COMMENT '消息接收的用户id',
    `data`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '消息内容',
    `is_read`     tinyint(4)                                                    NULL DEFAULT 0 COMMENT '已读-0 未读 ,1 已读',
    `create_time` datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`   tinyint(4)                                                    NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = Compact;

-- ----------------------------
-- 签到表
-- ----------------------------
DROP TABLE IF EXISTS `sign`;
CREATE TABLE `sign`
(
    `id`          bigint AUTO_INCREMENT COMMENT '主键' PRIMARY KEY,
    `user_id`     bigint     NOT NULL COMMENT '用户id',
    `sign_date`   DATE       NOT NULL COMMENT '签到的日期',
    `update_time` datetime   NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_time` datetime   NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_backup`   tinyint(1) NOT NULL default 0 COMMENT '是否补签 0-不补签 1-补签'
) comment '签到表' collate = utf8mb4_unicode_ci;


-- ----------------------------
-- 标签表
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`
(
    `id`          bigint(20)                                              NOT NULL AUTO_INCREMENT COMMENT 'id',
    `tag_name`    varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '标签名称',
    `user_id`     bigint(20)                                              NULL     DEFAULT NULL COMMENT '上传用户id',
    `parent_id`   bigint(20)                                              NULL     DEFAULT NULL COMMENT '父标签id',
    `is_parent`   tinyint(4)                                              NOT NULL COMMENT '0-不是父标签，1-父标签',
    `create_time` datetime                                                NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime                                                NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_delete`   tinyint(4)                                              NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uniIdx_tagName` (`tag_name`) USING BTREE,
    INDEX `Idx_userId` (`user_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- 队伍表
-- ----------------------------
DROP TABLE IF EXISTS `team`;
CREATE TABLE `team`
(
    `id`          bigint(20)                                                     NOT NULL AUTO_INCREMENT COMMENT 'id',
    `name`        varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '队伍名称',
    `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '描述',
    `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL COMMENT '封面图片',
    `max_num`     int(11)                                                        NOT NULL DEFAULT 1 COMMENT '最大人数',
    `expire_time` datetime                                                       NULL     DEFAULT NULL COMMENT '过期时间',
    `user_id`     bigint(20)                                                     NULL     DEFAULT NULL COMMENT '用户id',
    `status`      int(11)                                                        NOT NULL DEFAULT 0 COMMENT '0 - 公开，1 - 私有，2 - 加密',
    `password`    varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL COMMENT '密码',
    `create_time` datetime                                                       NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime                                                       NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_delete`   tinyint(4)                                                     NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 11
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '队伍'
  ROW_FORMAT = COMPACT;

-- ----------------------------
-- 用户表
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`           bigint(20)                                               NOT NULL AUTO_INCREMENT COMMENT 'id',
    `username`     varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL COMMENT '用户昵称',
    `password`     varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '用户密码',
    `user_account` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL COMMENT '账号',
    `avatar_url`   varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '用户头像',
    `gender`       tinyint(4)                                                        DEFAULT 1 NULL DEFAULT NULL COMMENT '性别 0-女 1-男 2-保密',
    `profile`      varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL,
    `phone`        varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL COMMENT '手机号',
    `email`        varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL COMMENT '邮箱',
    `status`       int(11)                                                  NULL     DEFAULT 0 COMMENT '用户状态，0为正常',
    `role`         int(11)                                                  NOT NULL DEFAULT 0 COMMENT '用户角色 0-普通用户,1-管理员',
    `friend_ids`   varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL,
    `tags`         varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '标签列表',
    `create_time`  datetime                                                 NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime                                                 NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`    tinyint(4)                                               NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uniIdx_account` (`user_account`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1000040
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- 用户队伍表
-- ----------------------------
DROP TABLE IF EXISTS `user_team`;
CREATE TABLE `user_team`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `user_id`     bigint(20) NULL     DEFAULT NULL COMMENT '用户id',
    `team_id`     bigint(20) NULL     DEFAULT NULL COMMENT '队伍id',
    `join_time`   datetime   NULL     DEFAULT NULL COMMENT '加入时间',
    `create_time` datetime   NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime   NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_delete`   tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 16
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '用户队伍关系'
  ROW_FORMAT = COMPACT;

SET FOREIGN_KEY_CHECKS = 1;
