# Re-OnlineJudge Backend · 开发日志与项目说明

## 概览
Spring Boot 2.6 + MyBatis-Plus 实现的在线判题/教学教务后端，当前提供用户体系、班级、日志、邮件、验证码等能力。数据源为 MySQL，测试场景使用 H2；认证基于 JWT，`ApiResponse{code,message,data}` 为统一返回结构。`re-oj.sql` 与 `schema-test.sql` 同步维护表结构，确保开发/测试一致。

## 近期迭代
- **2025-11-18**：完成“学生-班级绑定”链路：新增 `POST /api/student/classes/join`、`GET /api/student/classes`，学生必须凭邀请码加入且仅限一班；教师端 `/api/classes`、`/api/classes-members` 增加角色/归属校验，可移除班级成员；同步 `StudentClassControllerTest` 及相关控制器测试。
- **2025-11-17**：补充《Doc/测试规范》，沉淀 `ControllerTestSupport` 统一 MockMvc 场景、鉴权 Token 与 JSON 工具，所有控制器集成测试均迁移到该基类并去除固定种子依赖；扩展 `AuthControllerTest` 覆盖 `/auth/users/me`、注销、三角色登录、学生改/找回密码等路径，确保核心认证链路可验证；`./gradlew test` 在 JDK 11 下跑通（或升级 Gradle 以兼容更高版本 JDK）。
- **2025-11-16**：新增本地头像上传能力（正方形图片校验、静态资源映射）、补充 `app.storage` 配置、完善测试覆盖；同步 H2 Schema 至最新数据库结构；引入 `ApiException` + `ApiErrorCode`，统一业务异常返回，并在 `GlobalExceptionHandler` 中扩展数据库/权限/认证异常映射；所有基础 CRUD 接口改为抛出 `ApiException`（401/404 等主动返回 HTTP 状态），并新增了一套 DTO + `@Validated` 分组校验（Create/Update），实体不再直接承载请求校验逻辑；测试同步校验。
- **2025-11-15**：完成数据库扩展（announcement、discussion、homework、problem、submission 等表）并生成实体，准备后续接口开发。
- **2025-11-13**：实现注册 + 邮箱验证码、角色化登录、登录日志、学生/教师/管理员/班级/班级成员 CRUD 以及邮件通知。

## 功能模块
### 身份认证
- `POST /api/auth/register`（含可选邮箱验证）
- `POST /api/auth/login` / `POST /api/auth/logout`
- `GET /api/auth/users/me`
- 找回/修改密码：`/password/forgot/*`、`/password/change`
- 登录日志：`/api/login-logs`

### 用户与教务数据
- 学生、教师、管理员 CRUD：`/api/students | /api/teachers | /api/admins`
- 班级与班级成员：`/api/classes`、`/api/classes-members`（教师/管理员可管理，教师仅能操作自己创建的班级）
- 学生班级绑定：`POST /api/student/classes/join`、`GET /api/student/classes`，一生一班，并在服务层写入 `student.class_id` + `classes_member`
- 数据脚手架：`re-oj.sql` 含 announcement、discussion、homework、problem、submission 等新表，实体位于 `domain/entity`，待补业务接口。

### 文件服务（新增）
- 接口：`POST /api/files/avatar`（需登录，`multipart/form-data`，字段 `file`）
- 规则：只接受 `png/jpg/jpeg/gif/webp`，必须为正方形图片；使用 `UUID` 命名，保存于 `app.storage.avatar-dir`（默认 `./uploads/avatars`）
- 访问：上传成功返回 `{"url":"/files/avatars/<name>.png"}`，通过 Spring MVC 静态资源映射直接访问
- 校验：读取图片像素确认正方形，非图片或非正方形会返回 `400` 与失败原因

## 模块化开发流程
> 老项目功能全面、耦合度高，重构版按“身份 → 班级 → 课程作业 → 题目/讨论”等纵向切片推进，确保每条业务链都能单独上线。

1. **身份体系（已完成）**
   - 三角色注册/登录：`/api/auth/register|login|logout` 已统一返回 `ApiResponse`，支持可选邮箱校验、登录日志与邮件通知。
   - 学生密码服务：`/api/auth/password/change|forgot/*` 覆盖修改、找回、邮件提醒；`AuthControllerTest` 全量验证。
   - 输出：规范的 JWT 载荷、`/api/auth/users/me`、`ControllerTestSupport` 基建，可直接被后续模块复用。
2. **学生-班级绑定（已完成）**
   - 学生端：`POST /api/student/classes/join`、`GET /api/student/classes`，统一走 `StudentService.joinClass()`；限制“一生一班”，重复加入返回 409，测试用例 `StudentClassControllerTest` 覆盖成功/冲突路径。
   - 数据约束：`classes_member` 新增唯一索引 + Check 约束，`student.class_id` 同步更新，避免并发重复写入。
   - 教师端：`/api/classes`、`/api/classes-members` 仅教师/管理员可写，教师只能操作自己创建的班级，可移除班级成员；相关测试 `ClassesControllerTest`、`ClassesMemberControllerTest` 更新为教师身份。
   - 输出：学生的班级上下文可由后续课程/作业模块直接复用。
3. **教师创建班级 & 学生管理（待办）**
   - 需求：教师端 `/api/teacher/classes`（或沿用 `/api/classes` + 角色控制）支持创建/更新/删除；同时提供“查看班级成员/移除学生”等操作。
   - 实施步骤：
     - 扩展 `ClassesController` 加入“仅 teacher 角色可创建/更新/删除”“creatorId 绑定 AuthenticatedUser”逻辑；
     - 在 `ClassesMemberController` 增加“教师移除学生”“根据成员类型过滤”等接口；
     - 编写 `TeacherClassFlowTest`：教师登录→创建班级→学生加入→教师查询成员/删除成员；
     - 文档同步（Swagger + README），让前端知道新的 JSON 结构与错误码。
4. **后续模块（课程/作业/题目/讨论/统计）**
   - 按“一个业务场景一条链”推进，每条链包含 Entity → Mapper → Service → Controller → 测试 → 文档；
   - 例如课程/作业链路：教师创建课程 → 关联班级 → 布置作业 → 学生提交 → 教师统计，实施时可拆分为多个 PR；
   - 积极复用 `ControllerTestSupport`，保持新增模块从 Day1 起就有集成测试。

> 以上流程会持续写进每次迭代日志，确保团队清楚“当前在哪一模块”和“下一步是什么”。

## 配置要点
```
app:
  auth.require-email-verify: true|false
  logging.login.async: true|false
  storage:
    avatar-dir: ./uploads/avatars      # 可改为绝对路径
    avatar-url-prefix: /files/avatars  # 暴露的 URL 前缀
```
测试环境 (`application-test.yml`) 将 `avatar-dir` 指向 `./build/test-avatars`，并关闭邮件/验证码依赖。若更换目录，确保部署机器具备写权限。

## API 文档与调试平台
- **收益**：完善 Swagger/OpenAPI 注释后，可在 `/swagger-ui.html` 直接查看接口、字段与示例，配合 `Authorize` 按钮即可带 Bearer Token 调试；导出 `openapi.json`/Postman 集合可让前端和测试人员一键导入，减少手动拼接 curl。
- **当前状态**：所有 Controller 已补上 `@Operation`、`@Parameter`、DTO `@Schema` 描述，springdoc 已依赖配置；仍需收敛接口说明（示例值、错误码说明）并生成共享的调试集合。
- **下一步行动**：
  1. 在 `Doc/` 下维护 `openapi.json` 快照和 Postman/Thunder 集合，随主要版本更新。
  2. 对外发布实例时在网关限制 Swagger 访问，或仅保留 `/v3/api-docs` 供内部使用。
  3. CI 中增加 `./gradlew bootRun --args='--springdoc.api-docs.enabled=true'` + `curl /v3/api-docs` 步骤，自动产出最新 OpenAPI，并在 PR 模板中提示同步文档。
  4. 若前端需要快速体验，可在 README/HELP 中附 `swagger-ui` 路径与示例 Token 获取方式。

## 测试
- 流程指南：详见 `Doc/测试规范.md`，涵盖数据隔离、鉴权约束、断言规则；所有控制器测试继承 `ControllerTestSupport` 并通过统一注册/登录辅助方法生成唯一账号。
- `./gradlew test`：H2 + MockMvc 覆盖认证、用户/班级 CRUD、登录日志、头像上传、学生改/找回密码等流程；在当前 Gradle 版本下需使用 JDK 11（示例：`JAVA_HOME=/opt/homebrew/Cellar/openjdk@11/...`）。
- `FileControllerTest` 校验正方形成功、非正方形拒绝，运行后可在 `build/test-avatars` 查看落盘文件。
- 报告：`build/reports/tests/test/index.html`

## API 速查
| 模块 | 方法 & Path | 说明 |
| --- | --- | --- |
| 认证 | `POST /api/auth/register` | 注册学生账号（按配置决定是否需要邮箱激活） |
| 认证 | `POST /api/auth/login` | 支持 `student/teacher/admin` 角色登录 |
| 认证 | `GET /api/auth/users/me` | 返回 `AuthUserVO` |
| 密码 | `POST /api/auth/password/forgot/sendCode` | 找回密码-发送验证码 |
| 密码 | `POST /api/auth/password/forgot/verify` | 验证码 + 新密码 |
| 密码 | `POST /api/auth/password/change` | 登录学生修改密码 |
| 用户 | `GET/POST/PUT/DELETE /api/students` | 学生 CRUD（创建/更新自动哈希密码） |
| 用户 | `GET/POST/PUT/DELETE /api/teachers` | 教师 CRUD |
| 用户 | `GET/POST/PUT/DELETE /api/admins` | 管理员 CRUD |
| 班级 | `GET/POST/PUT/DELETE /api/classes` | 班级基础信息 |
| 班级成员 | `GET/POST/PUT/DELETE /api/classes-members` | 维护班级与学生/教师关系 |
| 学生班级 | `POST /api/student/classes/join` / `GET /api/student/classes` | 学生凭邀请码加入班级、查询当前班级 |
| 日志 | `GET /api/login-logs` | 支持按角色/用户过滤 |
| 文件 | `POST /api/files/avatar` | 上传正方形头像，返回 URL |

## 开发建议
1. **新模块脚手架**：实体已就绪，后续添加 Mapper → Service → Controller → Test 即可；推荐通过 MP Generator 批量生成（可扩展 `AvatarStorageService` 的写法）。
2. **数据库迁移**：长期建议引入 Flyway，将 `re-oj.sql` 拆分为版本化脚本；短期内修改结构请同步 `schema-test.sql`。
3. **文件扩展**：若需公告/讨论贴图，可复用 `AvatarStorageService` 思路，抽象出 `FileCategory`，并对上传类型/目录做差异化配置。
4. **安全**：虽然头像上传暂未限速，仍建议部署层添加基本防护（大小限制、WAF）；JWT 秘钥、邮件授权码等敏感配置请通过环境变量注入。
5. **异常与校验推广**：目前所有 CRUD 已迁移至 `ApiException` + DTO + `@Validated`，后续新增模块需维持同一规范；如引入更复杂的规则（例如成员类型与 studentId/teacherId 互斥），可在 DTO 层增加自定义校验器。

## 运行与部署提示
1. 准备 MySQL / Redis，导入 `re-oj.sql`
2. 配置 `application.yml` 或环境变量（包括 `app.storage` 目录）
3. 启动：`./gradlew bootRun` 或 `./gradlew build` + `java -jar`
4. 访问 Swagger UI：`http://localhost:8080/swagger-ui.html`，使用 `Authorize` 输入 `Bearer <token>`
5. 静态资源：头像上传后可直接访问 `http://<host>:8080/files/avatars/<name>.png`

## 路线图
- 题目/讨论/作业/提交接口实现与前端联调
- 文件服务扩展：通用附件上传、图片裁剪、CDN/对象存储切换
- 判题任务管理：与 Judge0 或自研沙箱集成
- 操作审计、风控与限流
- CI/CD：自动执行测试 & 构建镜像

如需更多模块设计或环境脚本，请继续提出需求。
