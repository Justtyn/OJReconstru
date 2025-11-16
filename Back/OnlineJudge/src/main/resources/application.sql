/*
 Navicat Premium Dump SQL

 Source Server         : Justyn's Macbook
 Source Server Type    : MySQL
 Source Server Version : 90200 (9.2.0)
 Source Host           : localhost:3306
 Source Schema         : re-oj

 Target Server Type    : MySQL
 Target Server Version : 90200 (9.2.0)
 File Encoding         : 65001

 Date: 16/11/2025 13:26:36
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id` bigint unsigned NOT NULL COMMENT 'id',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码(哈希)',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '姓名',
  `sex` enum('male','female','other','unknown') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'unknown' COMMENT '性别',
  `birth` date DEFAULT NULL COMMENT '出生日期',
  `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电话',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像',
  `last_login_ip` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最近登录IP',
  `last_login_time` datetime(3) DEFAULT NULL COMMENT '最近登录时间',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_admin_username` (`username`) USING BTREE,
  UNIQUE KEY `uk_admin_email` (`email`) USING BTREE,
  UNIQUE KEY `uk_admin_phone` (`phone`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- ----------------------------
-- Records of admin
-- ----------------------------
BEGIN;
INSERT INTO `admin` (`id`, `username`, `password`, `name`, `sex`, `birth`, `phone`, `email`, `avatar`, `last_login_ip`, `last_login_time`, `created_at`, `updated_at`) VALUES (1988830930425188354, 'admin', '$2a$10$77L45bgkKqqf3tRXH590ne2ML2Pa5hgv0jaYXnt4NQaNcYvV.hP6.', 'admin', 'male', '2003-05-28', '13850056409', '44739528@qq.com', NULL, '0:0:0:0:0:0:0:1', '2025-11-13 15:10:00.256', '2025-11-13 12:46:59.197', '2025-11-15 22:40:24.423');
COMMIT;

-- ----------------------------
-- Table structure for announcement
-- ----------------------------
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement` (
  `id` bigint unsigned NOT NULL COMMENT '公告ID',
  `title` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告内容',
  `time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '发布时间',
  `is_pinned` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否置顶',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_announcement_time` (`time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

-- ----------------------------
-- Records of announcement
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for classes
-- ----------------------------
DROP TABLE IF EXISTS `classes`;
CREATE TABLE `classes` (
  `id` bigint unsigned NOT NULL COMMENT '班级ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '班级名称',
  `creator_id` bigint unsigned DEFAULT NULL COMMENT '创建者',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '加入码',
  `start_date` date DEFAULT NULL COMMENT '开始日期',
  `end_date` date DEFAULT NULL COMMENT '结束日期',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '班级描述',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_class_code` (`code`) USING BTREE,
  KEY `idx_class_creator` (`creator_id`) USING BTREE,
  CONSTRAINT `fk_class_creator` FOREIGN KEY (`creator_id`) REFERENCES `teacher` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级表';

-- ----------------------------
-- Records of classes
-- ----------------------------
BEGIN;
INSERT INTO `classes` (`id`, `name`, `creator_id`, `code`, `start_date`, `end_date`, `description`, `created_at`, `updated_at`) VALUES (1988870655198408706, '2024级-软件工程(专升本)-1班', 1988829934890356738, 'SWEU2024', '2024-09-01', '2026-07-01', '2024级-软件工程(专升本)-1班', '2025-11-13 15:24:50.293', '2025-11-13 15:26:25.428');
COMMIT;

-- ----------------------------
-- Table structure for classes_member
-- ----------------------------
DROP TABLE IF EXISTS `classes_member`;
CREATE TABLE `classes_member` (
  `id` bigint unsigned NOT NULL COMMENT '记录ID',
  `class_id` bigint unsigned NOT NULL COMMENT '班级ID',
  `member_type` enum('student','teacher') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '成员类型',
  `student_id` bigint unsigned DEFAULT NULL COMMENT '学生ID',
  `teacher_id` bigint unsigned DEFAULT NULL COMMENT '教师ID',
  `joined_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '加入时间',
  `left_at` datetime(3) DEFAULT NULL COMMENT '离开时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_class_member_student` (`class_id`,`student_id`) USING BTREE,
  UNIQUE KEY `uk_class_member_teacher` (`class_id`,`teacher_id`) USING BTREE,
  UNIQUE KEY `uk_classes_member_student` (`student_id`,`member_type`),
  KEY `idx_class_member_type` (`member_type`) USING BTREE,
  KEY `fk_class_member_student` (`student_id`) USING BTREE,
  KEY `fk_class_member_teacher` (`teacher_id`) USING BTREE,
  CONSTRAINT `fk_class_member_class` FOREIGN KEY (`class_id`) REFERENCES `classes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_class_member_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_class_member_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级成员关系';

-- ----------------------------
-- Records of classes_member
-- ----------------------------
BEGIN;
INSERT INTO `classes_member` (`id`, `class_id`, `member_type`, `student_id`, `teacher_id`, `joined_at`, `left_at`) VALUES (1988872025452359682, 1988870655198408706, 'teacher', NULL, 1988829934890356738, '2025-11-13 15:30:16.986', NULL);
INSERT INTO `classes_member` (`id`, `class_id`, `member_type`, `student_id`, `teacher_id`, `joined_at`, `left_at`) VALUES (1988872299164250113, 1988870655198408706, 'student', 1988867822453567489, NULL, '2025-11-13 15:31:22.240', NULL);
COMMIT;

-- ----------------------------
-- Table structure for discussion
-- ----------------------------
DROP TABLE IF EXISTS `discussion`;
CREATE TABLE `discussion` (
  `id` bigint unsigned NOT NULL COMMENT '讨论ID',
  `user_id` bigint unsigned NOT NULL COMMENT '发布者ID',
  `problem_id` bigint unsigned DEFAULT NULL COMMENT '关联题目ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '发布时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '最后修改时间',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '讨论内容',
  `view_num` int unsigned NOT NULL DEFAULT '0' COMMENT '浏览量',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  PRIMARY KEY (`id`),
  KEY `idx_discuss_user` (`user_id`) USING BTREE,
  KEY `idx_discuss_problem` (`problem_id`) USING BTREE,
  KEY `idx_discuss_create_time` (`create_time`) USING BTREE,
  CONSTRAINT `fk_discuss_problem` FOREIGN KEY (`problem_id`) REFERENCES `problem` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_discuss_user` FOREIGN KEY (`user_id`) REFERENCES `student` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='讨论表';

-- ----------------------------
-- Records of discussion
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for discussion_comment
-- ----------------------------
DROP TABLE IF EXISTS `discussion_comment`;
CREATE TABLE `discussion_comment` (
  `id` bigint unsigned NOT NULL COMMENT '评论ID',
  `discuss_id` bigint unsigned NOT NULL COMMENT '所属讨论ID',
  `user_id` bigint unsigned DEFAULT NULL COMMENT '评论者ID',
  `parent_comment_id` bigint unsigned DEFAULT NULL COMMENT '父评论ID',
  `reply_to_user_id` bigint unsigned DEFAULT NULL COMMENT '回复给的用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '发布时间',
  PRIMARY KEY (`id`),
  KEY `idx_comment_discuss` (`discuss_id`) USING BTREE,
  KEY `idx_comment_user` (`user_id`) USING BTREE,
  KEY `idx_comment_parent` (`parent_comment_id`) USING BTREE,
  KEY `idx_comment_reply_to_user` (`reply_to_user_id`) USING BTREE,
  CONSTRAINT `fk_comment_discuss` FOREIGN KEY (`discuss_id`) REFERENCES `discussion` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_comment_parent` FOREIGN KEY (`parent_comment_id`) REFERENCES `discussion_comment` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_comment_reply_to_user` FOREIGN KEY (`reply_to_user_id`) REFERENCES `student` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `student` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='讨论评论表';

-- ----------------------------
-- Records of discussion_comment
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for homework
-- ----------------------------
DROP TABLE IF EXISTS `homework`;
CREATE TABLE `homework` (
  `id` bigint unsigned NOT NULL COMMENT '作业ID',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `class_id` bigint unsigned NOT NULL COMMENT '所属班级',
  `start_time` datetime(3) NOT NULL COMMENT '开始时间',
  `end_time` datetime(3) NOT NULL COMMENT '结束时间',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '作业说明',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  PRIMARY KEY (`id`),
  KEY `idx_homework_class` (`class_id`) USING BTREE,
  CONSTRAINT `fk_homework_class` FOREIGN KEY (`class_id`) REFERENCES `classes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='作业表';

-- ----------------------------
-- Records of homework
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for homework_problem
-- ----------------------------
DROP TABLE IF EXISTS `homework_problem`;
CREATE TABLE `homework_problem` (
  `homework_id` bigint unsigned NOT NULL COMMENT '作业ID',
  `problem_id` bigint unsigned NOT NULL COMMENT '题目ID',
  `ac_count` int unsigned NOT NULL DEFAULT '0' COMMENT '通过数',
  `submit_count` int unsigned NOT NULL DEFAULT '0' COMMENT '提交数',
  PRIMARY KEY (`homework_id`,`problem_id`) USING BTREE,
  KEY `idx_homework_problem_problem` (`problem_id`) USING BTREE,
  CONSTRAINT `fk_homework_problem_homework` FOREIGN KEY (`homework_id`) REFERENCES `homework` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_homework_problem_problem` FOREIGN KEY (`problem_id`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='作业题目表';

-- ----------------------------
-- Records of homework_problem
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for judge_status
-- ----------------------------
DROP TABLE IF EXISTS `judge_status`;
CREATE TABLE `judge_status` (
  `id` tinyint unsigned NOT NULL COMMENT 'Judge0 status.id',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内部代码，如 ACCEPTED',
  `description_en` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Judge0原始英文描述',
  `description_zh` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '中文描述，用于前端展示',
  `is_final` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否终态（不再变化）',
  `is_success` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否为通过状态',
  `is_compile_error` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否为编译错误',
  `is_judge_error` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否为判题系统内部错误',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='判题状态表（单测试点，映射Judge0）';

-- ----------------------------
-- Records of judge_status
-- ----------------------------
BEGIN;
INSERT INTO `judge_status` (`id`, `code`, `description_en`, `description_zh`, `is_final`, `is_success`, `is_compile_error`, `is_judge_error`, `sort_order`) VALUES (1, 'IN_QUEUE', 'In Queue', '排队中', 0, 0, 0, 0, 10);
INSERT INTO `judge_status` (`id`, `code`, `description_en`, `description_zh`, `is_final`, `is_success`, `is_compile_error`, `is_judge_error`, `sort_order`) VALUES (2, 'PROCESSING', 'Processing', '判题中', 0, 0, 0, 0, 20);
INSERT INTO `judge_status` (`id`, `code`, `description_en`, `description_zh`, `is_final`, `is_success`, `is_compile_error`, `is_judge_error`, `sort_order`) VALUES (3, 'ACCEPTED', 'Accepted', '通过', 1, 1, 0, 0, 30);
INSERT INTO `judge_status` (`id`, `code`, `description_en`, `description_zh`, `is_final`, `is_success`, `is_compile_error`, `is_judge_error`, `sort_order`) VALUES (4, 'WRONG_ANSWER', 'Wrong Answer', '答案错误', 1, 0, 0, 0, 40);
INSERT INTO `judge_status` (`id`, `code`, `description_en`, `description_zh`, `is_final`, `is_success`, `is_compile_error`, `is_judge_error`, `sort_order`) VALUES (5, 'TIME_LIMIT_EXCEEDED', 'Time Limit Exceeded', '时间超限', 1, 0, 0, 0, 50);
INSERT INTO `judge_status` (`id`, `code`, `description_en`, `description_zh`, `is_final`, `is_success`, `is_compile_error`, `is_judge_error`, `sort_order`) VALUES (6, 'COMPILATION_ERROR', 'Compilation Error', '编译错误', 1, 0, 1, 0, 60);
INSERT INTO `judge_status` (`id`, `code`, `description_en`, `description_zh`, `is_final`, `is_success`, `is_compile_error`, `is_judge_error`, `sort_order`) VALUES (7, 'RUNTIME_ERROR', 'Runtime Error', '运行时错误', 1, 0, 0, 0, 70);
INSERT INTO `judge_status` (`id`, `code`, `description_en`, `description_zh`, `is_final`, `is_success`, `is_compile_error`, `is_judge_error`, `sort_order`) VALUES (8, 'MEMORY_LIMIT_EXCEEDED', 'Memory Limit Exceeded', '内存超限', 1, 0, 0, 0, 80);
INSERT INTO `judge_status` (`id`, `code`, `description_en`, `description_zh`, `is_final`, `is_success`, `is_compile_error`, `is_judge_error`, `sort_order`) VALUES (13, 'INTERNAL_ERROR', 'Internal Error', '判题系统内部错误', 1, 0, 0, 1, 130);
COMMIT;

-- ----------------------------
-- Table structure for login_log
-- ----------------------------
DROP TABLE IF EXISTS `login_log`;
CREATE TABLE `login_log` (
  `id` bigint unsigned NOT NULL COMMENT '主键ID',
  `role` enum('admin','teacher','student') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录角色',
  `user_id` bigint unsigned NOT NULL COMMENT '对应用户表中的ID',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名快照，便于追踪历史',
  `ip_address` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '登录IP地址',
  `location` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '登录地理位置',
  `user_agent` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '浏览器或客户端标识',
  `device` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '设备信息（如iPhone/PC等）',
  `login_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '登录时间',
  `logout_time` datetime(3) DEFAULT NULL COMMENT '登出时间',
  `success` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否登录成功',
  `fail_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '失败原因（如密码错误、封禁等）',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '记录创建时间',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '记录更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_role_user` (`role`,`user_id`),
  KEY `idx_login_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录日志表';

-- ----------------------------
-- Records of login_log
-- ----------------------------
BEGIN;
INSERT INTO `login_log` (`id`, `role`, `user_id`, `username`, `ip_address`, `location`, `user_agent`, `device`, `login_time`, `logout_time`, `success`, `fail_reason`, `created_at`, `updated_at`) VALUES (1988788136839069697, 'student', 1988788136704851969, 'test01', '0:0:0:0:0:0:0:1', NULL, 'PostmanRuntime/7.49.0', 'Other', '2025-11-13 09:56:56.374', '2025-11-13 10:31:14.816', 1, NULL, '2025-11-13 09:56:56.379', '2025-11-13 09:56:56.379');
INSERT INTO `login_log` (`id`, `role`, `user_id`, `username`, `ip_address`, `location`, `user_agent`, `device`, `login_time`, `logout_time`, `success`, `fail_reason`, `created_at`, `updated_at`) VALUES (1988794598239879169, 'student', 1988788136704851969, 'test01', '0:0:0:0:0:0:0:1', NULL, 'PostmanRuntime/7.50.0', 'Other', '2025-11-13 10:22:36.893', '2025-11-13 10:31:09.601', 1, NULL, '2025-11-13 10:22:36.898', '2025-11-13 10:22:36.898');
INSERT INTO `login_log` (`id`, `role`, `user_id`, `username`, `ip_address`, `location`, `user_agent`, `device`, `login_time`, `logout_time`, `success`, `fail_reason`, `created_at`, `updated_at`) VALUES (1988797521271308290, 'student', 1988788136704851969, 'test01', '0:0:0:0:0:0:0:1', NULL, 'PostmanRuntime/7.50.0', 'Other', '2025-11-13 10:34:13.799', NULL, 1, NULL, '2025-11-13 10:34:13.807', '2025-11-13 10:34:13.807');
INSERT INTO `login_log` (`id`, `role`, `user_id`, `username`, `ip_address`, `location`, `user_agent`, `device`, `login_time`, `logout_time`, `success`, `fail_reason`, `created_at`, `updated_at`) VALUES (1988822859812544514, 'student', 1988788136704851969, 'test01', '0:0:0:0:0:0:0:1', NULL, 'PostmanRuntime/7.50.0', 'Other', '2025-11-13 12:14:54.970', NULL, 1, NULL, '2025-11-13 12:14:54.983', '2025-11-13 12:14:54.983');
INSERT INTO `login_log` (`id`, `role`, `user_id`, `username`, `ip_address`, `location`, `user_agent`, `device`, `login_time`, `logout_time`, `success`, `fail_reason`, `created_at`, `updated_at`) VALUES (1988830433316278274, 'teacher', 1988829934890356738, '王三三', '0:0:0:0:0:0:0:1', NULL, 'PostmanRuntime/7.50.0', 'Other', '2025-11-13 12:45:00.639', NULL, 0, '密码错误', '2025-11-13 12:45:00.650', '2025-11-13 12:45:00.650');
INSERT INTO `login_log` (`id`, `role`, `user_id`, `username`, `ip_address`, `location`, `user_agent`, `device`, `login_time`, `logout_time`, `success`, `fail_reason`, `created_at`, `updated_at`) VALUES (1988830458335301634, 'teacher', 1988829934890356738, '王三三', '0:0:0:0:0:0:0:1', NULL, 'PostmanRuntime/7.50.0', 'Other', '2025-11-13 12:45:06.608', NULL, 1, NULL, '2025-11-13 12:45:06.609', '2025-11-13 12:45:06.609');
INSERT INTO `login_log` (`id`, `role`, `user_id`, `username`, `ip_address`, `location`, `user_agent`, `device`, `login_time`, `logout_time`, `success`, `fail_reason`, `created_at`, `updated_at`) VALUES (1988831484320780290, 'admin', 1988830930425188354, 'admin', '0:0:0:0:0:0:0:1', NULL, 'PostmanRuntime/7.50.0', 'Other', '2025-11-13 12:49:11.221', NULL, 1, NULL, '2025-11-13 12:49:11.224', '2025-11-13 12:49:11.224');
INSERT INTO `login_log` (`id`, `role`, `user_id`, `username`, `ip_address`, `location`, `user_agent`, `device`, `login_time`, `logout_time`, `success`, `fail_reason`, `created_at`, `updated_at`) VALUES (1988837065639108609, 'admin', 1988830930425188354, 'admin', '0:0:0:0:0:0:0:1', NULL, 'PostmanRuntime/7.50.0', 'Other', '2025-11-13 13:11:21.907', NULL, 1, NULL, '2025-11-13 13:11:21.912', '2025-11-13 13:11:21.912');
INSERT INTO `login_log` (`id`, `role`, `user_id`, `username`, `ip_address`, `location`, `user_agent`, `device`, `login_time`, `logout_time`, `success`, `fail_reason`, `created_at`, `updated_at`) VALUES (1988837206391562242, 'student', 1988788136704851969, 'test01', '0:0:0:0:0:0:0:1', NULL, 'PostmanRuntime/7.50.0', 'Other', '2025-11-13 13:11:55.470', NULL, 1, NULL, '2025-11-13 13:11:55.471', '2025-11-13 13:11:55.471');
INSERT INTO `login_log` (`id`, `role`, `user_id`, `username`, `ip_address`, `location`, `user_agent`, `device`, `login_time`, `logout_time`, `success`, `fail_reason`, `created_at`, `updated_at`) VALUES (1988837286632792065, 'teacher', 1988829934890356738, '王三三', '0:0:0:0:0:0:0:1', NULL, 'PostmanRuntime/7.50.0', 'Other', '2025-11-13 13:12:14.600', NULL, 1, NULL, '2025-11-13 13:12:14.600', '2025-11-13 13:12:14.600');
INSERT INTO `login_log` (`id`, `role`, `user_id`, `username`, `ip_address`, `location`, `user_agent`, `device`, `login_time`, `logout_time`, `success`, `fail_reason`, `created_at`, `updated_at`) VALUES (1988861975971278849, 'student', 0, 'test02', '0:0:0:0:0:0:0:1', NULL, 'PostmanRuntime/7.50.0', 'Other', '2025-11-13 14:50:20.991', NULL, 1, 'register_pending', '2025-11-13 14:50:21.005', '2025-11-13 14:50:21.005');
INSERT INTO `login_log` (`id`, `role`, `user_id`, `username`, `ip_address`, `location`, `user_agent`, `device`, `login_time`, `logout_time`, `success`, `fail_reason`, `created_at`, `updated_at`) VALUES (1988863377074335746, 'student', 1988863371948896258, 'test02', '0:0:0:0:0:0:0:1', NULL, 'PostmanRuntime/7.50.0', 'Other', '2025-11-13 14:55:55.046', NULL, 1, NULL, '2025-11-13 14:55:55.049', '2025-11-13 14:55:55.049');
INSERT INTO `login_log` (`id`, `role`, `user_id`, `username`, `ip_address`, `location`, `user_agent`, `device`, `login_time`, `logout_time`, `success`, `fail_reason`, `created_at`, `updated_at`) VALUES (1988863695057104897, 'student', 1988863371948896258, 'test02', '0:0:0:0:0:0:0:1', NULL, 'PostmanRuntime/7.50.0', 'Other', '2025-11-13 14:57:10.859', '2025-11-13 14:57:38.458', 1, NULL, '2025-11-13 14:57:10.860', '2025-11-13 14:57:10.860');
INSERT INTO `login_log` (`id`, `role`, `user_id`, `username`, `ip_address`, `location`, `user_agent`, `device`, `login_time`, `logout_time`, `success`, `fail_reason`, `created_at`, `updated_at`) VALUES (1988866865246285826, 'teacher', 1988829934890356738, '王三三', '0:0:0:0:0:0:0:1', NULL, 'PostmanRuntime/7.50.0', 'Other', '2025-11-13 15:09:46.690', NULL, 1, NULL, '2025-11-13 15:09:46.693', '2025-11-13 15:09:46.693');
INSERT INTO `login_log` (`id`, `role`, `user_id`, `username`, `ip_address`, `location`, `user_agent`, `device`, `login_time`, `logout_time`, `success`, `fail_reason`, `created_at`, `updated_at`) VALUES (1988866922162991105, 'admin', 1988830930425188354, 'admin', '0:0:0:0:0:0:0:1', NULL, 'PostmanRuntime/7.50.0', 'Other', '2025-11-13 15:10:00.261', NULL, 1, NULL, '2025-11-13 15:10:00.262', '2025-11-13 15:10:00.262');
INSERT INTO `login_log` (`id`, `role`, `user_id`, `username`, `ip_address`, `location`, `user_agent`, `device`, `login_time`, `logout_time`, `success`, `fail_reason`, `created_at`, `updated_at`) VALUES (1988867723363135489, 'student', 0, 'test01', '0:0:0:0:0:0:0:1', NULL, 'PostmanRuntime/7.50.0', 'Other', '2025-11-13 15:13:11.281', NULL, 1, 'register_pending', '2025-11-13 15:13:11.289', '2025-11-13 15:13:11.289');
INSERT INTO `login_log` (`id`, `role`, `user_id`, `username`, `ip_address`, `location`, `user_agent`, `device`, `login_time`, `logout_time`, `success`, `fail_reason`, `created_at`, `updated_at`) VALUES (1988867826958249985, 'student', 1988867822453567489, 'test01', '0:0:0:0:0:0:0:1', NULL, 'PostmanRuntime/7.50.0', 'Other', '2025-11-13 15:13:35.975', NULL, 1, NULL, '2025-11-13 15:13:35.984', '2025-11-13 15:13:35.984');
INSERT INTO `login_log` (`id`, `role`, `user_id`, `username`, `ip_address`, `location`, `user_agent`, `device`, `login_time`, `logout_time`, `success`, `fail_reason`, `created_at`, `updated_at`) VALUES (1988867898919923713, 'student', 1988867822453567489, 'test01', '0:0:0:0:0:0:0:1', NULL, 'PostmanRuntime/7.50.0', 'Other', '2025-11-13 15:13:53.138', NULL, 1, NULL, '2025-11-13 15:13:53.139', '2025-11-13 15:13:53.139');
INSERT INTO `login_log` (`id`, `role`, `user_id`, `username`, `ip_address`, `location`, `user_agent`, `device`, `login_time`, `logout_time`, `success`, `fail_reason`, `created_at`, `updated_at`) VALUES (1988890243156000770, 'student', 1988863371948896258, 'test02', '0:0:0:0:0:0:0:1', NULL, 'PostmanRuntime/7.50.0', 'Other', '2025-11-13 16:42:40.415', NULL, 1, NULL, '2025-11-13 16:42:40.420', '2025-11-13 16:42:40.420');
INSERT INTO `login_log` (`id`, `role`, `user_id`, `username`, `ip_address`, `location`, `user_agent`, `device`, `login_time`, `logout_time`, `success`, `fail_reason`, `created_at`, `updated_at`) VALUES (1989911009209114625, 'student', 1988867822453567489, 'test01', '0:0:0:0:0:0:0:1', NULL, 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36', 'Other', '2025-11-16 12:18:50.008', NULL, 1, NULL, '2025-11-16 12:18:50.012', '2025-11-16 12:18:50.012');
INSERT INTO `login_log` (`id`, `role`, `user_id`, `username`, `ip_address`, `location`, `user_agent`, `device`, `login_time`, `logout_time`, `success`, `fail_reason`, `created_at`, `updated_at`) VALUES (1989921894342434817, 'student', 1988867822453567489, 'test01', '0:0:0:0:0:0:0:1', NULL, 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36', 'Other', '2025-11-16 13:02:05.225', NULL, 1, NULL, '2025-11-16 13:02:05.230', '2025-11-16 13:02:05.230');
COMMIT;

-- ----------------------------
-- Table structure for problem
-- ----------------------------
DROP TABLE IF EXISTS `problem`;
CREATE TABLE `problem` (
  `id` bigint unsigned NOT NULL COMMENT '题号',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '问题名称',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '出题时间',
  `ac_count` int unsigned NOT NULL DEFAULT '0' COMMENT '通过数量',
  `submit_count` int unsigned NOT NULL DEFAULT '0' COMMENT '提交数量',
  `desc` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '题目描述',
  `desc_input` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '输入描述',
  `desc_output` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '输出描述',
  `sample_input` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '输入样例',
  `sample_output` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '输出样例',
  `hint` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '提示说明',
  `daily_challenge` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '0' COMMENT '是否日常挑战题',
  `difficulty` enum('easy','medium','hard') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'easy' COMMENT '难度',
  `time_limit_ms` int unsigned NOT NULL DEFAULT '1000' COMMENT '时间限制(ms)',
  `memory_limit_kb` int unsigned NOT NULL DEFAULT '131072' COMMENT '内存限制(KB)',
  `source` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '题目来源',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_problem_difficulty` (`difficulty`) USING BTREE,
  KEY `idx_problem_active` (`is_active`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='题目表';

-- ----------------------------
-- Records of problem
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for problem_testcase
-- ----------------------------
DROP TABLE IF EXISTS `problem_testcase`;
CREATE TABLE `problem_testcase` (
  `id` bigint unsigned NOT NULL COMMENT '测试用例ID',
  `problem_id` bigint unsigned NOT NULL COMMENT '题目ID',
  `input_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '输入数据',
  `output_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '输出数据',
  PRIMARY KEY (`id`),
  KEY `idx_testcase_problem` (`problem_id`) USING BTREE,
  CONSTRAINT `fk_testcase_problem` FOREIGN KEY (`problem_id`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='题目测试用例表';

-- ----------------------------
-- Records of problem_testcase
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for solution
-- ----------------------------
DROP TABLE IF EXISTS `solution`;
CREATE TABLE `solution` (
  `id` bigint unsigned NOT NULL COMMENT '题解ID',
  `user_id` bigint unsigned NOT NULL COMMENT '发布者ID',
  `problem_id` bigint unsigned NOT NULL COMMENT '题目ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '题解标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '题解内容',
  `language` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '解题语言',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  PRIMARY KEY (`id`),
  KEY `idx_solution_user` (`user_id`) USING BTREE,
  KEY `idx_solution_problem` (`problem_id`) USING BTREE,
  CONSTRAINT `fk_solution_problem` FOREIGN KEY (`problem_id`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_solution_user` FOREIGN KEY (`user_id`) REFERENCES `student` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='题解表';

-- ----------------------------
-- Records of solution
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for student
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
  `id` bigint unsigned NOT NULL COMMENT 'id',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码(哈希)',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '姓名',
  `sex` enum('male','female','other','unknown') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'unknown' COMMENT '性别',
  `birth` date DEFAULT NULL COMMENT '出生日期',
  `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电话',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像',
  `background` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '背景图片',
  `ac` int unsigned NOT NULL DEFAULT '0' COMMENT '通过数',
  `submit` int unsigned NOT NULL DEFAULT '0' COMMENT '提交数',
  `school` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '学校',
  `score` int DEFAULT '0' COMMENT '得分',
  `last_login_time` datetime(3) DEFAULT NULL COMMENT '上次登录时间',
  `last_language` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '上次提交的语言',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '账号创建时间',
  `last_visit_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '上次访问时间',
  `daily_challenge` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '0' COMMENT '日常挑战状态',
  `register_ip` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '注册IP',
  `last_login_ip` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最近登录IP',
  `bio` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '个人简介',
  `is_verified` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否完成邮箱验证',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_student_username` (`username`) USING BTREE,
  UNIQUE KEY `uk_student_email` (`email`) USING BTREE,
  CONSTRAINT `student_chk_1` CHECK ((`ac` <= `submit`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生表';

-- ----------------------------
-- Records of student
-- ----------------------------
BEGIN;
INSERT INTO `student` (`id`, `username`, `password`, `name`, `sex`, `birth`, `phone`, `email`, `avatar`, `background`, `ac`, `submit`, `school`, `score`, `last_login_time`, `last_language`, `create_time`, `last_visit_time`, `daily_challenge`, `register_ip`, `last_login_ip`, `bio`, `is_verified`, `updated_at`) VALUES (1988863371948896258, 'test02', '$2a$10$fch9PZDV7lwf35dcEc8omOfZamDIzh5yknzIy/tNtArW3zdNg2UL.', 'test02', 'unknown', NULL, NULL, 'togjustyn@gmail.com', NULL, NULL, 0, 0, NULL, 0, '2025-11-13 16:42:40.398', NULL, '2025-11-13 14:55:53.871', '2025-11-13 14:55:53.871', '0', NULL, '0:0:0:0:0:0:0:1', NULL, 1, '2025-11-13 14:55:53.871');
INSERT INTO `student` (`id`, `username`, `password`, `name`, `sex`, `birth`, `phone`, `email`, `avatar`, `background`, `ac`, `submit`, `school`, `score`, `last_login_time`, `last_language`, `create_time`, `last_visit_time`, `daily_challenge`, `register_ip`, `last_login_ip`, `bio`, `is_verified`, `updated_at`) VALUES (1988867822453567489, 'test01', '$2a$10$SiePywtYR3kK6MbCtDFFJOvOzJrEeSWdDQvN5tJMyRZbtUzsuiYMi', 'test01', 'unknown', NULL, NULL, '1046220903@qq.com', NULL, NULL, 0, 0, NULL, 0, '2025-11-16 13:02:05.172', NULL, '2025-11-13 15:13:34.917', '2025-11-13 15:13:34.917', '0', NULL, '0:0:0:0:0:0:0:1', NULL, 1, '2025-11-13 15:13:34.917');
COMMIT;

-- ----------------------------
-- Table structure for submission
-- ----------------------------
DROP TABLE IF EXISTS `submission`;
CREATE TABLE `submission` (
  `id` bigint unsigned NOT NULL COMMENT '提交ID',
  `student_id` bigint unsigned NOT NULL COMMENT '提交学生ID',
  `problem_id` bigint unsigned NOT NULL COMMENT '题目ID',
  `homework_id` bigint unsigned DEFAULT NULL COMMENT '所属作业ID（可选）',
  `language_id` int NOT NULL COMMENT 'Judge0语言ID',
  `source_code` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户提交的源代码',
  `overall_status_id` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '整体提交状态，关联submission_overall_status',
  `passed_case_count` int unsigned NOT NULL DEFAULT '0' COMMENT '通过的测试用例数量',
  `total_case_count` int unsigned NOT NULL DEFAULT '0' COMMENT '总测试用例数量',
  `score` int NOT NULL DEFAULT '0' COMMENT '得分（预留字段）',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '提交时间',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_submission_student` (`student_id`),
  KEY `idx_submission_problem` (`problem_id`),
  KEY `idx_submission_homework` (`homework_id`),
  KEY `idx_submission_overall_status` (`overall_status_id`),
  CONSTRAINT `fk_submission_homework` FOREIGN KEY (`homework_id`) REFERENCES `homework` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_submission_overall_status` FOREIGN KEY (`overall_status_id`) REFERENCES `submission_overall_status` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_submission_problem` FOREIGN KEY (`problem_id`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_submission_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='提交表（一次提交）';

-- ----------------------------
-- Records of submission
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for submission_overall_status
-- ----------------------------
DROP TABLE IF EXISTS `submission_overall_status`;
CREATE TABLE `submission_overall_status` (
  `id` tinyint unsigned NOT NULL COMMENT '整体提交状态ID',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态编码，如 PENDING',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '前端展示名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '说明',
  `is_success` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否表示整体通过',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_submission_overall_status_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='整体提交状态表（OJ 自定义）';

-- ----------------------------
-- Records of submission_overall_status
-- ----------------------------
BEGIN;
INSERT INTO `submission_overall_status` (`id`, `code`, `name`, `description`, `is_success`) VALUES (1, 'PENDING', '排队中', '等待发送到判题机或等待执行', 0);
INSERT INTO `submission_overall_status` (`id`, `code`, `name`, `description`, `is_success`) VALUES (2, 'RUNNING', '判题中', '测试用例仍在执行', 0);
INSERT INTO `submission_overall_status` (`id`, `code`, `name`, `description`, `is_success`) VALUES (3, 'ACCEPTED', '通过', '所有测试用例均通过', 1);
INSERT INTO `submission_overall_status` (`id`, `code`, `name`, `description`, `is_success`) VALUES (4, 'PARTIAL_WRONG', '部分错误', '部分测试用例未通过', 0);
INSERT INTO `submission_overall_status` (`id`, `code`, `name`, `description`, `is_success`) VALUES (5, 'WRONG', '错误', '所有测试用例均未通过或关键测试未通过', 0);
INSERT INTO `submission_overall_status` (`id`, `code`, `name`, `description`, `is_success`) VALUES (6, 'SYSTEM_ERROR', '系统错误', '调用判题机或内部异常', 0);
COMMIT;

-- ----------------------------
-- Table structure for submission_testcase_result
-- ----------------------------
DROP TABLE IF EXISTS `submission_testcase_result`;
CREATE TABLE `submission_testcase_result` (
  `id` bigint unsigned NOT NULL COMMENT '主键ID',
  `submission_id` bigint unsigned NOT NULL COMMENT '提交ID',
  `testcase_id` bigint unsigned NOT NULL COMMENT '题目测试用例ID',
  `judge0_token` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Judge0返回的token',
  `status_id` tinyint unsigned NOT NULL COMMENT 'Judge0判题状态ID，对应judge_status.id',
  `time_used` decimal(10,3) DEFAULT NULL COMMENT '耗时（秒），对应Judge0的time',
  `memory_used` int unsigned DEFAULT NULL COMMENT '内存使用（KB），对应Judge0的memory',
  `stdout` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '程序标准输出',
  `stderr` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '错误输出',
  `compile_output` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '编译错误信息',
  `message` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '额外信息（如内部错误信息）',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '记录创建时间',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '记录更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_submission_testcase` (`submission_id`,`testcase_id`),
  KEY `idx_str_submission` (`submission_id`),
  KEY `idx_str_testcase` (`testcase_id`),
  KEY `idx_str_status` (`status_id`),
  CONSTRAINT `fk_str_status` FOREIGN KEY (`status_id`) REFERENCES `judge_status` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_str_submission` FOREIGN KEY (`submission_id`) REFERENCES `submission` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_str_testcase` FOREIGN KEY (`testcase_id`) REFERENCES `problem_testcase` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='提交-测试用例判题结果表';

-- ----------------------------
-- Records of submission_testcase_result
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for teacher
-- ----------------------------
DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher` (
  `id` bigint unsigned NOT NULL COMMENT 'id',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码(哈希)',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '姓名',
  `sex` enum('male','female','other','unknown') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'unknown' COMMENT '性别',
  `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电话',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职称',
  `last_login_time` datetime(3) DEFAULT NULL COMMENT '最近登录时间',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_teacher_username` (`username`) USING BTREE,
  UNIQUE KEY `uk_teacher_email` (`email`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教师表';

-- ----------------------------
-- Records of teacher
-- ----------------------------
BEGIN;
INSERT INTO `teacher` (`id`, `username`, `password`, `name`, `sex`, `phone`, `email`, `avatar`, `title`, `last_login_time`, `created_at`, `updated_at`) VALUES (1988829934890356738, '王三三', '$2a$10$uCSUMCmFtDX0pful5IMYoOP0uS6e4s4pVVD0eQH2UwWFIp80e5XiG', '王三三', 'female', '13850000000', 'togjustyn@gmail.com', NULL, NULL, '2025-11-13 15:09:46.647', '2025-11-13 12:43:01.935', '2025-11-13 15:09:11.902');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
