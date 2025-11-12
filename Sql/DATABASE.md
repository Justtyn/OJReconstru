## Re-OnlineJudge 数据库结构说明

本说明介绍 `Sql/OnlineJudge.sql` 中的全量结构，以便在重构期间对数据层进行统一管理。整体目标：在保留原有业务字段的同时增加审计信息、唯一约束、状态位和辅助表，使判题及教学场景更加健壮并支持后续扩展。下面按表逐一列出字段、约束与用途。

---

### 1. 全局约定
- **字符集**：所有表统一使用 `utf8mb4` + `utf8mb4_unicode_ci`，保证多语言数据兼容。
- **时间精度**：绝大部分业务时间列提升为 `datetime(3)`，方便毫秒级审计。
- **主键类型**：核心业务表使用 `int unsigned` 自增，日志/成员/判题等高频表使用 `bigint unsigned`。
- **审计字段**：新增 `created_at`、`updated_at`、`deleted_at`（按需）列，便于排查与审计。
- **状态枚举**：大量原字符串列改为枚举，如 `status`, `sex`, `member_type` 等，减少脏数据。
- **CHECK 约束**：仅在符合 MySQL 约束限制的场景使用（例如 `access_log.duration_ms >= 0`、`student.ac <= student.submit` 等）。

---

### 2. 表级说明（字段列表）

> 类型格式：`类型(长度)`，若为 `NULL` 则允许空。默认值、索引、约束另行说明。

#### 2.1 `access_log`
| 列名 | 类型 | 约束 | 说明 |
| --- | --- | --- | --- |
| id | bigint unsigned | PK, AI | 访问日志主键 |
| request_time / response_time | datetime(3) | NOT NULL | 请求进入/完成时间 |
| duration_ms | int unsigned | NOT NULL, CHECK >=0 | 请求耗时 |
| method | varchar(16) | NOT NULL | HTTP 方法 |
| scheme/host/port/uri/path/query_string/route_pattern | varchar |  | 入口信息 |
| http_status | int | NOT NULL | 状态码 |
| referer/user_agent | varchar(512) |  | UA 信息 |
| client_ip/client_ip_src/client_ips | varchar |  | 客户端 IP 解析结果 |
| user_id | bigint | FK (业务 ID) | 关联用户 |
| user_type | enum(student,teacher,admin) |  | 用户类型 |
| username/role | varchar |  | 用户信息 |
| headers | json |  | 允许存储白名单请求头 |
| request_body/response_body | longtext |  | 请求/响应体 |
| response_bytes | bigint unsigned | DEFAULT 0 | 响应大小 |
| error_stack | text |  | 异常栈 |
| body_digest | char(64) |  | 请求体哈希 |
| trace_id/request_id/app/env/instance | varchar | `request_id` 唯一索引 | 追踪信息 |
| ip_country/ip_region/ip_city/ip_isp | varchar |  | 地理信息 |
| is_alert | tinyint(1) | DEFAULT 0 | 是否告警 |
| alert_reason | varchar(128) |  | 告警原因 |
| created_at/updated_at | datetime(3) | DEFAULT current_timestamp(3) | 审计时间 |

索引：`uk_access_request_id`、`idx_request_time`、`idx_status_time`、`idx_uri_time`、`idx_user_time`、`idx_client_ip_time`、`idx_trace_id`。

#### 2.2 `admin`
| 列名 | 类型 | 约束 | 说明 |
| --- | --- | --- | --- |
| id | int unsigned | PK, AI |
| username | varchar(64) | NOT NULL, UNIQUE |
| password | varchar(255) | NOT NULL | 哈希值 |
| name | varchar(100) |
| sex | enum(male,female,other,unknown) | DEFAULT unknown |
| birth | date |
| phone | varchar(32) | UNIQUE |
| email | varchar(255) | UNIQUE |
| avatar | varchar(512) |
| role | varchar(64) | DEFAULT 'admin' |
| status | enum(active,suspended,disabled) | DEFAULT active |
| last_login_ip | varchar(45) |
| last_login_at | datetime(3) |
| two_factor_secret | varchar(255) |
| created_at/updated_at | datetime(3) | 自动时间戳 |

#### 2.3 `score`（班级）
字段：`id`(PK)、`name`、`creator_id`(FK→admin)、`homework_quantity`、`code`(唯一加入码)、`term`、`status`、`start_date`、`end_date`、`max_student`、`description`、`created_at`、`updated_at`、`archived_at`。索引：`uk_score_code`、`idx_score_creator`。

#### 2.4 `student`
| 列名 | 类型 | 说明 |
| --- | --- | --- |
| id | int unsigned PK |
| username/password/name/sex/birth/phone/email/avatar/role/background | 同 admin 逻辑 |
| ac/submit | int unsigned | CHECK `ac <= submit` |
| school/score/last_login_time/last_language |
| create_time/last_visit_time | datetime(3) |
| class_id | int unsigned FK → score |
| daily_challenge | varchar(255) |
| status | enum(active,suspended,graduated,deleted) |
| password_salt/register_ip/last_login_ip/bio | 安全/简介 |
| is_verified | tinyint(1) |
| deleted_at/updated_at | datetime(3) |

索引：`uk_student_username`、`uk_student_email`、`idx_student_class`、`idx_student_status`。

#### 2.5 `teacher`
字段与 `student` 类似：`id`、`username`(唯一)、`password`、`name`、`sex`、`phone`、`email`、`avatar`、`role`、`class_id`(FK→score)、`title`、`status`、`last_login_at`、`created_at`、`updated_at`。索引：`uk_teacher_username`、`uk_teacher_email`、`idx_teacher_class`。

#### 2.6 `class_member`
| 列名 | 类型 | 说明 |
| --- | --- | --- |
| id | bigint unsigned PK |
| class_id | int unsigned FK → score |
| member_type | enum(student,teacher,assistant) |
| student_id / teacher_id | int unsigned FK，可为 NULL |
| role | varchar(64) | 班内角色 |
| joined_at / left_at | datetime(3) |

索引：`uk_class_member_student`（class_id + student_id）、`uk_class_member_teacher`、`idx_class_member_type`。用于统一管理所有班级成员。

#### 2.7 `announcement`
字段：`id`、`title`、`content`、`time`、`publisher_id`(FK→admin)、`is_pinned`、`visibility`、`class_id`(FK→score)、`start_time`、`end_time`、`updated_at`。索引：`idx_announcement_class`、`idx_announcement_time`。

#### 2.8 `oj_problem`
| 列名 | 类型 | 说明 |
| --- | --- | --- |
| id | int unsigned PK |
| name/setter/create_time |
| ac_count/submit_count | 统计 |
| desc/desc_input/desc_output/sample_input/sample_output/hint | longtext/text 内容 |
| daily_challenge | varchar(255) |
| difficulty | enum(easy,medium,hard) |
| time_limit_ms/memory_limit_kb | unsigned 限制 |
| source | varchar(255) |
| is_active | tinyint(1) |
| slug | varchar(128) UNIQUE |
| updated_at | datetime(3) |

索引：`idx_problem_difficulty`、`idx_problem_active`。

#### 2.9 `problem_tag`
字段：`id`、`name`(唯一)、`description`、`created_at`。

#### 2.10 `problem_tag_relation`
复合主键 (`problem_id`,`tag_id`)，外键指向 `oj_problem` 和 `problem_tag`，并有 `idx_tag_relation_tag` 辅助索引。

#### 2.11 `problem_testcase`
| 列名 | 类型 | 说明 |
| --- | --- | --- |
| id | bigint unsigned PK |
| problem_id | FK → oj_problem |
| type | enum(sample,hidden) |
| input_data/output_data | longtext |
| order_index | int unsigned | 控制展示或判题顺序 |
| score_ratio | int unsigned | 分数占比 |

#### 2.12 `discuss`
字段：`id`、`user_id`(FK→student)、`problem_id`(FK→oj_problem, 可空)、`title`、`create_time`、`update_time`、`content`、`view_num`、`status`、`last_comment_time`、`comment_count`、`is_pinned`。索引覆盖用户、题目、创建时间。

#### 2.13 `discuss_comment`
字段：`id`、`discuss_id`(FK→discuss)、`user_id`(FK→student)、`parent_comment_id`(自引用)、`reply_to_user_id`(FK→student)、`content`、`create_time`、`like_count`、`status`、`updated_at`。索引覆盖讨论、用户、父评论、回复目标。

#### 2.14 `homework`
字段：`id`、`title`、`class_id`(FK→score)、`start_time`、`end_time`、`description`、`status`、`time_limit_ms`、`memory_limit_kb`、`total_score`、`late_penalty_rule`、`creator_id`(FK→teacher)、`created_at`、`updated_at`。

#### 2.15 `homework_problem`
复合主键 (`homework_id`,`problem_id`)；字段还包括 `ac_count`、`submit_count`、`full_score`、`order_index`、`partial_allowed`。外键指向 `homework` 与 `oj_problem`。

#### 2.16 `oj_status`
| 列名 | 类型 | 说明 |
| --- | --- | --- |
| id | bigint unsigned PK |
| problem_id | FK → oj_problem |
| user_id | FK → student |
| username | varchar(255) |
| time | int unsigned | 运行耗时(ms) |
| creat_time | datetime(3) | 提交时间（沿用原字段名） |
| language / language_version | varchar |
| memory | int unsigned | KB |
| status | enum(多种判题结果) |
| code | longtext |
| output / compile_output | mediumtext |
| judge_message | varchar(512) |
| score | int unsigned |
| submit_ip | varchar(45) |
| judge_task_id | varchar(64) |
| judge_at | datetime(3) |

索引：`idx_oj_status_problem`、`idx_oj_status_user`、`idx_oj_status_status_time`。

#### 2.17 `oj_status_detail`
字段：`id`、`status_id`(FK→oj_status)、`testcase_id`(FK→problem_testcase，可空)、`sequence`、`result`、`runtime_ms`、`memory_kb`、`stdout`、`stderr`、`created_at`。索引：`idx_status_detail_status`、`idx_status_detail_testcase`。

#### 2.18 `homework_submission`
| 列名 | 类型 | 说明 |
| --- | --- | --- |
| id | bigint unsigned PK |
| homework_id | FK → homework |
| problem_id | FK → oj_problem |
| student_id | FK → student |
| oj_status_id | FK → oj_status |
| attempt_no | int unsigned | 同一题目第几次尝试 |
| score | int unsigned |
| status | enum(pending,judging,accepted,rejected,expired) |
| is_final | tinyint(1) | 是否计入最终成绩 |
| submitted_at / last_judged_at | datetime(3) |

唯一索引：`uk_homework_submission_attempt`（homework_id, problem_id, student_id, attempt_no）。额外索引：`idx_homework_submission_student`、`idx_homework_submission_status`。

#### 2.19 `solution`
字段：`id`、`user_id`(FK→student)、`problem_id`(FK→oj_problem)、`title`、`content`、`language`、`create_time`、`updated_at`、`like_num`、`status`、`visibility`；索引覆盖用户与题目。

---

### 3. 关系示意（核心 FK）
- `score` ← `admin`（创建者）
- `student` / `teacher` → `score`（所属班级，可空）
- `class_member` → `score`、`student`、`teacher`
- `announcement` → `admin`、`score`
- `homework` → `score`、`teacher`
- `homework_problem` → `homework`、`oj_problem`
- `homework_submission` → `homework`、`oj_problem`、`student`、`oj_status`
- `discuss` / `solution` → `student`、`oj_problem`
- `discuss_comment` → `discuss`、`student`（含自引用）
- `oj_status` → `student`、`oj_problem`
- `oj_status_detail` → `oj_status`、`problem_testcase`
- `problem_tag_relation` → `oj_problem`、`problem_tag`

---

### 4. 使用建议
1. **迁移顺序**：执行 `DROP`→`CREATE` 脚本前务必备份旧数据，可按新结构逐表导入，或先建空表再编写迁移脚本。
2. **应用升级**：更新 ORM/DAO 映射以涵盖新字段与枚举，并在业务层补充 `class_member`、`problem_tag`、`homework_submission` 等新实体。
3. **数据校验**：部分约束（如 `class_member` 只能关联学生或教师）改由应用层或触发器控制，导入时需要自行确保数据合法。
4. **索引策略**：新增索引覆盖常见查询维度（用户、时间、状态等）；若查询模式改变，可在该文件基础上继续优化。

本说明与 `OnlineJudge.sql` 保持同步，若结构再次调整，请同步更新此文档。
