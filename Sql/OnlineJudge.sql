/*
 Re-OnlineJudge schema rebuild
 Generated for project refactor: adds stricter constraints, auditing columns, and supporting tables
 while keeping legacy attributes so the application can evolve safely.
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- drop tables in reverse dependency order to avoid FK conflicts
DROP TABLE IF EXISTS `oj_status_detail`;
DROP TABLE IF EXISTS `homework_submission`;
DROP TABLE IF EXISTS `class_member`;
DROP TABLE IF EXISTS `problem_testcase`;
DROP TABLE IF EXISTS `problem_tag_relation`;
DROP TABLE IF EXISTS `problem_tag`;
DROP TABLE IF EXISTS `solution`;
DROP TABLE IF EXISTS `discuss_comment`;
DROP TABLE IF EXISTS `discuss`;
DROP TABLE IF EXISTS `announcement`;
DROP TABLE IF EXISTS `homework_problem`;
DROP TABLE IF EXISTS `homework`;
DROP TABLE IF EXISTS `oj_status`;
DROP TABLE IF EXISTS `oj_problem`;
DROP TABLE IF EXISTS `student`;
DROP TABLE IF EXISTS `teacher`;
DROP TABLE IF EXISTS `score`;
DROP TABLE IF EXISTS `admin`;
DROP TABLE IF EXISTS `access_log`;

-- ----------------------------
-- Table structure for access_log
-- ----------------------------
CREATE TABLE `access_log` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `request_time` datetime(3) NOT NULL COMMENT '请求进入时间',
  `response_time` datetime(3) NOT NULL COMMENT '请求完成时间',
  `duration_ms` int unsigned NOT NULL COMMENT '耗时(ms)',
  `method` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'HTTP方法',
  `scheme` varchar(8) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '协议(http/https)',
  `host` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '主机名',
  `port` int DEFAULT NULL COMMENT '端口',
  `uri` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '完整URI(含路径与查询)',
  `path` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '路径(不含查询)',
  `query_string` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '原始查询串',
  `route_pattern` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '匹配的路由模式',
  `http_status` int NOT NULL COMMENT 'HTTP状态码',
  `referer` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Referer',
  `user_agent` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'User-Agent',
  `client_ip` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '解析出的真实客户端IP',
  `client_ip_src` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IP来源标记',
  `client_ips` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'X-Forwarded-For完整链路',
  `user_id` bigint DEFAULT NULL COMMENT '业务用户ID',
  `user_type` enum('student','teacher','admin') COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户类型',
  `username` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户名',
  `role` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色/权限标识',
  `headers` json DEFAULT NULL COMMENT '请求头',
  `request_body` longtext COLLATE utf8mb4_unicode_ci COMMENT '请求体',
  `response_body` longtext COLLATE utf8mb4_unicode_ci COMMENT '响应体',
  `response_bytes` bigint unsigned NOT NULL DEFAULT '0' COMMENT '响应大小(字节)',
  `error_stack` text COLLATE utf8mb4_unicode_ci COMMENT '异常堆栈(可选)',
  `body_digest` char(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求体哈希',
  `trace_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '链路追踪ID',
  `request_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求ID',
  `app` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '应用名',
  `env` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '环境',
  `instance` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '实例/节点标识',
  `ip_country` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IP国家',
  `ip_region` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IP省/州',
  `ip_city` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IP城市',
  `ip_isp` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '运营商/组织',
  `is_alert` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否触发告警阈值',
  `alert_reason` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '告警原因描述',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_access_request_id` (`request_id`),
  KEY `idx_request_time` (`request_time`),
  KEY `idx_status_time` (`http_status`,`request_time`),
  KEY `idx_uri_time` (`path`,`request_time`),
  KEY `idx_user_time` (`user_id`,`request_time`),
  KEY `idx_client_ip_time` (`client_ip`,`request_time`),
  KEY `idx_trace_id` (`trace_id`),
  CHECK (`response_time` >= `request_time`),
  CHECK (`duration_ms` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='统一访问日志';

-- ----------------------------
-- Table structure for admin
-- ----------------------------
CREATE TABLE `admin` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` varchar(64) NOT NULL COMMENT '账号',
  `password` varchar(255) NOT NULL COMMENT '密码(哈希)',
  `name` varchar(100) DEFAULT NULL COMMENT '姓名',
  `sex` enum('male','female','other','unknown') NOT NULL DEFAULT 'unknown' COMMENT '性别',
  `birth` date DEFAULT NULL COMMENT '出生日期',
  `phone` varchar(32) DEFAULT NULL COMMENT '电话',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(512) DEFAULT NULL COMMENT '头像',
  `role` varchar(64) NOT NULL DEFAULT 'admin' COMMENT '身份标识',
  `status` enum('active','suspended','disabled') NOT NULL DEFAULT 'active' COMMENT '账号状态',
  `last_login_ip` varchar(45) DEFAULT NULL COMMENT '最近登录IP',
  `last_login_at` datetime(3) DEFAULT NULL COMMENT '最近登录时间',
  `two_factor_secret` varchar(255) DEFAULT NULL COMMENT '双因素认证密钥',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_admin_username` (`username`),
  UNIQUE KEY `uk_admin_email` (`email`),
  UNIQUE KEY `uk_admin_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- ----------------------------
-- Table structure for score (class)
-- ----------------------------
CREATE TABLE `score` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '班级ID',
  `name` varchar(255) NOT NULL COMMENT '班级名称',
  `creator_id` int unsigned DEFAULT NULL COMMENT '创建者',
  `homework_quantity` int NOT NULL DEFAULT '0' COMMENT '作业数量',
  `code` varchar(32) DEFAULT NULL COMMENT '加入码',
  `term` varchar(32) DEFAULT NULL COMMENT '学期',
  `status` enum('draft','active','archived') NOT NULL DEFAULT 'active' COMMENT '班级状态',
  `start_date` date DEFAULT NULL COMMENT '开始日期',
  `end_date` date DEFAULT NULL COMMENT '结束日期',
  `max_student` int unsigned DEFAULT NULL COMMENT '最大学生数',
  `description` text COMMENT '班级描述',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `archived_at` datetime(3) DEFAULT NULL COMMENT '归档时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_score_code` (`code`),
  KEY `idx_score_creator` (`creator_id`),
  CONSTRAINT `fk_score_creator` FOREIGN KEY (`creator_id`) REFERENCES `admin` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级表';

-- ----------------------------
-- Table structure for student
-- ----------------------------
CREATE TABLE `student` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` varchar(64) NOT NULL COMMENT '账号',
  `password` varchar(255) NOT NULL COMMENT '密码(哈希)',
  `name` varchar(100) DEFAULT NULL COMMENT '姓名',
  `sex` enum('male','female','other','unknown') DEFAULT 'unknown' COMMENT '性别',
  `birth` date DEFAULT NULL COMMENT '出生日期',
  `phone` varchar(32) DEFAULT NULL COMMENT '电话',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(512) DEFAULT NULL COMMENT '头像',
  `role` varchar(64) NOT NULL DEFAULT 'student' COMMENT '身份标识',
  `background` varchar(512) DEFAULT NULL COMMENT '背景图片',
  `ac` int unsigned NOT NULL DEFAULT '0' COMMENT '通过数',
  `submit` int unsigned NOT NULL DEFAULT '0' COMMENT '提交数',
  `school` varchar(255) DEFAULT NULL COMMENT '学校',
  `score` int DEFAULT '0' COMMENT '得分',
  `last_login_time` datetime(3) DEFAULT NULL COMMENT '上次登录时间',
  `last_language` varchar(64) DEFAULT NULL COMMENT '上次提交的语言',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '账号创建时间',
  `last_visit_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '上次访问时间',
  `class_id` int unsigned DEFAULT NULL COMMENT '所属班级ID',
  `daily_challenge` varchar(255) DEFAULT '0' COMMENT '日常挑战状态',
  `status` enum('active','suspended','graduated','deleted') NOT NULL DEFAULT 'active' COMMENT '账号状态',
  `password_salt` varchar(64) DEFAULT NULL COMMENT '密码盐',
  `register_ip` varchar(45) DEFAULT NULL COMMENT '注册IP',
  `last_login_ip` varchar(45) DEFAULT NULL COMMENT '最近登录IP',
  `bio` varchar(1024) DEFAULT NULL COMMENT '个人简介',
  `is_verified` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否完成实名/邮箱验证',
  `deleted_at` datetime(3) DEFAULT NULL COMMENT '删除时间',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_username` (`username`),
  UNIQUE KEY `uk_student_email` (`email`),
  KEY `idx_student_class` (`class_id`),
  KEY `idx_student_status` (`status`),
  CONSTRAINT `fk_student_class` FOREIGN KEY (`class_id`) REFERENCES `score` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CHECK (`ac` <= `submit`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生表';

-- ----------------------------
-- Table structure for teacher
-- ----------------------------
CREATE TABLE `teacher` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` varchar(64) NOT NULL COMMENT '账号',
  `password` varchar(255) NOT NULL COMMENT '密码(哈希)',
  `name` varchar(100) DEFAULT NULL COMMENT '姓名',
  `sex` enum('male','female','other','unknown') DEFAULT 'unknown' COMMENT '性别',
  `phone` varchar(32) DEFAULT NULL COMMENT '电话',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(512) DEFAULT NULL COMMENT '头像',
  `role` varchar(64) NOT NULL DEFAULT 'teacher' COMMENT '身份标识',
  `class_id` int unsigned DEFAULT NULL COMMENT '所属主班级ID',
  `title` varchar(128) DEFAULT NULL COMMENT '职称',
  `status` enum('active','inactive','disabled') NOT NULL DEFAULT 'active' COMMENT '状态',
  `last_login_at` datetime(3) DEFAULT NULL COMMENT '最近登录时间',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_teacher_username` (`username`),
  UNIQUE KEY `uk_teacher_email` (`email`),
  KEY `idx_teacher_class` (`class_id`),
  CONSTRAINT `fk_teacher_class` FOREIGN KEY (`class_id`) REFERENCES `score` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教师表';

-- ----------------------------
-- Table structure for class_member
-- ----------------------------
CREATE TABLE `class_member` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `class_id` int unsigned NOT NULL COMMENT '班级ID',
  `member_type` enum('student','teacher','assistant') NOT NULL COMMENT '成员类型',
  `student_id` int unsigned DEFAULT NULL COMMENT '学生ID',
  `teacher_id` int unsigned DEFAULT NULL COMMENT '教师ID',
  `role` varchar(64) DEFAULT NULL COMMENT '班级内角色',
  `joined_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '加入时间',
  `left_at` datetime(3) DEFAULT NULL COMMENT '离开时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_class_member_student` (`class_id`,`student_id`),
  UNIQUE KEY `uk_class_member_teacher` (`class_id`,`teacher_id`),
  KEY `idx_class_member_type` (`member_type`),
  CONSTRAINT `fk_class_member_class` FOREIGN KEY (`class_id`) REFERENCES `score` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_class_member_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_class_member_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级成员关系';

-- ----------------------------
-- Table structure for announcement
-- ----------------------------
CREATE TABLE `announcement` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(150) NOT NULL COMMENT '公告标题',
  `content` text NOT NULL COMMENT '公告内容',
  `time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '发布时间',
  `publisher_id` int unsigned DEFAULT NULL COMMENT '发布人(管理员)',
  `is_pinned` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否置顶',
  `visibility` enum('global','class','student') NOT NULL DEFAULT 'global' COMMENT '可见范围',
  `class_id` int unsigned DEFAULT NULL COMMENT '关联班级',
  `start_time` datetime(3) DEFAULT NULL COMMENT '生效时间',
  `end_time` datetime(3) DEFAULT NULL COMMENT '失效时间',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_announcement_class` (`class_id`),
  KEY `idx_announcement_time` (`time`),
  CONSTRAINT `fk_announcement_admin` FOREIGN KEY (`publisher_id`) REFERENCES `admin` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_announcement_class` FOREIGN KEY (`class_id`) REFERENCES `score` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

-- ----------------------------
-- Table structure for oj_problem
-- ----------------------------
CREATE TABLE `oj_problem` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '题号',
  `name` varchar(255) NOT NULL COMMENT '问题名称',
  `setter` varchar(255) NOT NULL COMMENT '出题人',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '出题时间',
  `ac_count` int unsigned NOT NULL DEFAULT '0' COMMENT '通过数量',
  `submit_count` int unsigned NOT NULL DEFAULT '0' COMMENT '提交数量',
  `desc` longtext NOT NULL COMMENT '题目描述',
  `desc_input` text NOT NULL COMMENT '输入描述',
  `desc_output` text NOT NULL COMMENT '输出描述',
  `sample_input` text NOT NULL COMMENT '输入样例',
  `sample_output` text NOT NULL COMMENT '输出样例',
  `hint` text NOT NULL COMMENT '提示说明',
  `daily_challenge` varchar(255) DEFAULT '0' COMMENT '是否日常挑战题',
  `difficulty` enum('easy','medium','hard') NOT NULL DEFAULT 'easy' COMMENT '难度',
  `time_limit_ms` int unsigned NOT NULL DEFAULT '1000' COMMENT '时间限制(ms)',
  `memory_limit_kb` int unsigned NOT NULL DEFAULT '131072' COMMENT '内存限制(KB)',
  `source` varchar(255) DEFAULT NULL COMMENT '题目来源',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `slug` varchar(128) DEFAULT NULL COMMENT '题目短链',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_problem_slug` (`slug`),
  KEY `idx_problem_difficulty` (`difficulty`),
  KEY `idx_problem_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目表';

-- ----------------------------
-- Table structure for problem_tag
-- ----------------------------
CREATE TABLE `problem_tag` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` varchar(64) NOT NULL COMMENT '标签名',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_problem_tag_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目标签';

-- ----------------------------
-- Table structure for problem_tag_relation
-- ----------------------------
CREATE TABLE `problem_tag_relation` (
  `problem_id` int unsigned NOT NULL COMMENT '题目ID',
  `tag_id` int unsigned NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`problem_id`,`tag_id`),
  KEY `idx_tag_relation_tag` (`tag_id`),
  CONSTRAINT `fk_problem_tag_problem` FOREIGN KEY (`problem_id`) REFERENCES `oj_problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_problem_tag_tag` FOREIGN KEY (`tag_id`) REFERENCES `problem_tag` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目-标签关联';

-- ----------------------------
-- Table structure for problem_testcase
-- ----------------------------
CREATE TABLE `problem_testcase` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '测试用例ID',
  `problem_id` int unsigned NOT NULL COMMENT '题目ID',
  `type` enum('sample','hidden') NOT NULL DEFAULT 'hidden' COMMENT '用例类型',
  `input_data` longtext NOT NULL COMMENT '输入数据',
  `output_data` longtext NOT NULL COMMENT '输出数据',
  `order_index` int unsigned NOT NULL DEFAULT '1' COMMENT '顺序',
  `score_ratio` int unsigned NOT NULL DEFAULT '100' COMMENT '计分占比',
  PRIMARY KEY (`id`),
  KEY `idx_testcase_problem` (`problem_id`),
  CONSTRAINT `fk_testcase_problem` FOREIGN KEY (`problem_id`) REFERENCES `oj_problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目测试用例';

-- ----------------------------
-- Table structure for discuss
-- ----------------------------
CREATE TABLE `discuss` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '讨论ID',
  `user_id` int unsigned NOT NULL COMMENT '发布者ID',
  `problem_id` int unsigned DEFAULT NULL COMMENT '关联题目ID',
  `title` varchar(255) NOT NULL COMMENT '标题',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '发布时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '最后修改时间',
  `content` text COMMENT '讨论内容',
  `view_num` int unsigned NOT NULL DEFAULT '0' COMMENT '浏览量',
  `status` enum('published','archived','hidden') NOT NULL DEFAULT 'published' COMMENT '状态',
  `last_comment_time` datetime(3) DEFAULT NULL COMMENT '最近回复时间',
  `comment_count` int unsigned NOT NULL DEFAULT '0' COMMENT '评论数',
  `is_pinned` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否置顶',
  PRIMARY KEY (`id`),
  KEY `idx_discuss_user` (`user_id`),
  KEY `idx_discuss_problem` (`problem_id`),
  KEY `idx_discuss_create_time` (`create_time`),
  CONSTRAINT `fk_discuss_problem` FOREIGN KEY (`problem_id`) REFERENCES `oj_problem` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_discuss_user` FOREIGN KEY (`user_id`) REFERENCES `student` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='讨论表';

-- ----------------------------
-- Table structure for discuss_comment
-- ----------------------------
CREATE TABLE `discuss_comment` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `discuss_id` int unsigned NOT NULL COMMENT '所属讨论ID',
  `user_id` int unsigned NOT NULL COMMENT '评论者ID',
  `parent_comment_id` int unsigned DEFAULT NULL COMMENT '父评论ID',
  `reply_to_user_id` int unsigned DEFAULT NULL COMMENT '回复给的用户ID',
  `content` text NOT NULL COMMENT '评论内容',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '发布时间',
  `like_count` int unsigned NOT NULL DEFAULT '0' COMMENT '点赞数',
  `status` enum('visible','hidden','deleted') NOT NULL DEFAULT 'visible' COMMENT '状态',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_comment_discuss` (`discuss_id`),
  KEY `idx_comment_user` (`user_id`),
  KEY `idx_comment_parent` (`parent_comment_id`),
  KEY `idx_comment_reply_to_user` (`reply_to_user_id`),
  CONSTRAINT `fk_comment_discuss` FOREIGN KEY (`discuss_id`) REFERENCES `discuss` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_comment_parent` FOREIGN KEY (`parent_comment_id`) REFERENCES `discuss_comment` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_comment_reply_to_user` FOREIGN KEY (`reply_to_user_id`) REFERENCES `student` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `student` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='讨论评论表';

-- ----------------------------
-- Table structure for homework
-- ----------------------------
CREATE TABLE `homework` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '作业ID',
  `title` varchar(128) NOT NULL COMMENT '标题',
  `class_id` int unsigned NOT NULL COMMENT '所属班级',
  `start_time` datetime(3) NOT NULL COMMENT '开始时间',
  `end_time` datetime(3) NOT NULL COMMENT '结束时间',
  `description` text COMMENT '作业说明',
  `status` enum('draft','ongoing','closed') NOT NULL DEFAULT 'draft' COMMENT '状态',
  `time_limit_ms` int unsigned DEFAULT NULL COMMENT '额外时间限制',
  `memory_limit_kb` int unsigned DEFAULT NULL COMMENT '额外内存限制',
  `total_score` int unsigned NOT NULL DEFAULT '100' COMMENT '总分',
  `late_penalty_rule` varchar(255) DEFAULT NULL COMMENT '迟交规则',
  `creator_id` int unsigned DEFAULT NULL COMMENT '创建教师',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_homework_class` (`class_id`),
  KEY `idx_homework_creator` (`creator_id`),
  CONSTRAINT `fk_homework_class` FOREIGN KEY (`class_id`) REFERENCES `score` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_homework_creator` FOREIGN KEY (`creator_id`) REFERENCES `teacher` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作业表';

-- ----------------------------
-- Table structure for homework_problem
-- ----------------------------
CREATE TABLE `homework_problem` (
  `homework_id` int unsigned NOT NULL COMMENT '作业ID',
  `problem_id` int unsigned NOT NULL COMMENT '题目ID',
  `ac_count` int unsigned NOT NULL DEFAULT '0' COMMENT '通过数',
  `submit_count` int unsigned NOT NULL DEFAULT '0' COMMENT '提交数',
  `full_score` int unsigned NOT NULL DEFAULT '100' COMMENT '该题满分',
  `order_index` int unsigned NOT NULL DEFAULT '1' COMMENT '作业内排序',
  `partial_allowed` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否允许部分分',
  PRIMARY KEY (`homework_id`,`problem_id`),
  KEY `idx_homework_problem_problem` (`problem_id`),
  CONSTRAINT `fk_homework_problem_homework` FOREIGN KEY (`homework_id`) REFERENCES `homework` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_homework_problem_problem` FOREIGN KEY (`problem_id`) REFERENCES `oj_problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作业题目关联表';

-- ----------------------------
-- Table structure for homework_submission
-- ----------------------------
CREATE TABLE `oj_status` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '提交ID',
  `problem_id` int unsigned NOT NULL COMMENT '题目ID',
  `user_id` int unsigned NOT NULL COMMENT '用户ID',
  `username` varchar(255) DEFAULT NULL COMMENT '账号名称',
  `time` int unsigned DEFAULT NULL COMMENT '运行时间(ms)',
  `creat_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '提交时间',
  `language` varchar(64) NOT NULL COMMENT '语言',
  `language_version` varchar(64) DEFAULT NULL COMMENT '语言版本',
  `memory` int unsigned DEFAULT NULL COMMENT '内存(KB)',
  `status` enum('pending','running','accepted','wrong_answer','time_limit_exceeded','memory_limit_exceeded','runtime_error','compile_error','system_error','presentation_error','skipped') NOT NULL DEFAULT 'pending' COMMENT '状态',
  `code` longtext COMMENT '代码',
  `output` mediumtext COMMENT '输出信息',
  `judge_message` varchar(512) DEFAULT NULL COMMENT '判题机返回信息',
  `compile_output` mediumtext COMMENT '编译输出',
  `score` int unsigned DEFAULT '0' COMMENT '得分',
  `submit_ip` varchar(45) DEFAULT NULL COMMENT '提交IP',
  `judge_task_id` varchar(64) DEFAULT NULL COMMENT '判题任务ID',
  `judge_at` datetime(3) DEFAULT NULL COMMENT '判题完成时间',
  PRIMARY KEY (`id`),
  KEY `idx_oj_status_problem` (`problem_id`),
  KEY `idx_oj_status_user` (`user_id`),
  KEY `idx_oj_status_status_time` (`status`,`creat_time`),
  CONSTRAINT `fk_oj_status_problem` FOREIGN KEY (`problem_id`) REFERENCES `oj_problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_oj_status_student` FOREIGN KEY (`user_id`) REFERENCES `student` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提交状态表';

-- ----------------------------
-- Table structure for oj_status_detail
-- ----------------------------
CREATE TABLE `oj_status_detail` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `status_id` bigint unsigned NOT NULL COMMENT '提交ID',
  `testcase_id` bigint unsigned DEFAULT NULL COMMENT '测试用例ID',
  `sequence` int unsigned NOT NULL DEFAULT '1' COMMENT '测试顺序',
  `result` enum('accepted','wrong_answer','time_limit_exceeded','memory_limit_exceeded','runtime_error','compile_error','skipped') NOT NULL COMMENT '测试结果',
  `runtime_ms` int unsigned DEFAULT NULL COMMENT '运行时间',
  `memory_kb` int unsigned DEFAULT NULL COMMENT '内存',
  `stdout` mediumtext COMMENT '用例输出',
  `stderr` mediumtext COMMENT '错误输出',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '记录时间',
  PRIMARY KEY (`id`),
  KEY `idx_status_detail_status` (`status_id`),
  KEY `idx_status_detail_testcase` (`testcase_id`),
  CONSTRAINT `fk_status_detail_status` FOREIGN KEY (`status_id`) REFERENCES `oj_status` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_status_detail_testcase` FOREIGN KEY (`testcase_id`) REFERENCES `problem_testcase` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提交判题明细';

-- ----------------------------
-- Table structure for homework_submission
-- ----------------------------
CREATE TABLE `homework_submission` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '提交记录ID',
  `homework_id` int unsigned NOT NULL COMMENT '作业ID',
  `problem_id` int unsigned NOT NULL COMMENT '题目ID',
  `student_id` int unsigned NOT NULL COMMENT '学生ID',
  `oj_status_id` bigint unsigned DEFAULT NULL COMMENT '判题记录ID',
  `attempt_no` int unsigned NOT NULL DEFAULT '1' COMMENT '尝试次数',
  `score` int unsigned NOT NULL DEFAULT '0' COMMENT '得分',
  `status` enum('pending','judging','accepted','rejected','expired') NOT NULL DEFAULT 'pending' COMMENT '状态',
  `is_final` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否最终提交',
  `submitted_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '提交时间',
  `last_judged_at` datetime(3) DEFAULT NULL COMMENT '最后判题时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_homework_submission_attempt` (`homework_id`,`problem_id`,`student_id`,`attempt_no`),
  KEY `idx_homework_submission_student` (`student_id`),
  KEY `idx_homework_submission_status` (`status`),
  CONSTRAINT `fk_homework_submission_homework` FOREIGN KEY (`homework_id`) REFERENCES `homework` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_homework_submission_problem` FOREIGN KEY (`problem_id`) REFERENCES `oj_problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_homework_submission_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_homework_submission_status` FOREIGN KEY (`oj_status_id`) REFERENCES `oj_status` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作业提交表';

-- ----------------------------
-- Table structure for solution
-- ----------------------------
CREATE TABLE `solution` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '题解ID',
  `user_id` int unsigned NOT NULL COMMENT '发布者ID',
  `problem_id` int unsigned NOT NULL COMMENT '题目ID',
  `title` varchar(255) DEFAULT NULL COMMENT '题解标题',
  `content` longtext COMMENT '题解内容',
  `language` varchar(64) DEFAULT NULL COMMENT '解题语言',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `like_num` int unsigned NOT NULL DEFAULT '0' COMMENT '点赞数',
  `status` enum('draft','published','archived') NOT NULL DEFAULT 'published' COMMENT '状态',
  `visibility` enum('public','class','private') NOT NULL DEFAULT 'public' COMMENT '可见范围',
  PRIMARY KEY (`id`),
  KEY `idx_solution_user` (`user_id`),
  KEY `idx_solution_problem` (`problem_id`),
  CONSTRAINT `fk_solution_user` FOREIGN KEY (`user_id`) REFERENCES `student` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_solution_problem` FOREIGN KEY (`problem_id`) REFERENCES `oj_problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题解表';

SET FOREIGN_KEY_CHECKS = 1;
