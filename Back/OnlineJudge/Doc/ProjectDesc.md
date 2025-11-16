# Re-OnlineJudge Backend · 开发日志与项目说明

## 概览
Spring Boot 2.6 + MyBatis-Plus 实现的在线判题/教学教务后端，当前提供用户体系、班级、日志、邮件、验证码等能力。数据源为 MySQL，测试场景使用 H2；认证基于 JWT，`ApiResponse{code,message,data}` 为统一返回结构。`re-oj.sql` 与 `schema-test.sql` 同步维护表结构，确保开发/测试一致。

- **2025-11-20**：补全“公告 + 题解 + 作业 + 讨论”模块：管理员独占 `/api/admin/announcements` 的新增/更新/删除，学生/教师可经 `/api/announcements` 查看列表和详情；题解模块提供 `/api/solutions`/`/api/problems/{id}/solutions` 查询、学生 `/api/problems/{id}/solutions` 发布、作者或管理员 `PUT/DELETE /api/solutions/{id}` 维护；实现 `/api/homeworks` 作业 CRUD 与题目管理（创建时必须携带 `problemIds`，教师限管自己班级）；讨论支持学生发帖/评论、作者或管理员维护；新增 `AnnouncementControllerTest`、`SolutionControllerTest`、`HomeworkControllerTest`、`DiscussionControllerTest` 覆盖关键信道与权限分支。
- **2025-11-19**：落地题库 & 测试用例模块：新增公开的 `GET /api/problems`、`GET /api/problems/{id}` 列表/详情，后台教师&管理员可通过 `/api/admin/problems` 进行 CRUD，并管理 `/api/admin/problems/{id}/testcases` 及 `/api/admin/problem-testcases/{testcaseId}`；删除题目会同步清理由 `problem_testcase` 表维护的真实评测数据；补充 `ProblemControllerTest` 覆盖公开查询、测试用例 CRUD 与级联删除。
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

### 题库 & 测试用例（新增）
- 公开列表/详情：`GET /api/problems` 支持按关键字、难度、日常挑战筛选，只返回 `isActive=true` 的题目；`GET /api/problems/{id}` 提供完整描述，未启用题目仅教师/管理员可见。
- 后台管理：教师或管理员通过 `POST/PUT/DELETE /api/admin/problems` 维护题干信息，并可在 `/api/admin/problems/{id}/testcases` 下维护一题多测试用例，对单个用例提供 `PUT/DELETE /api/admin/problem-testcases/{testcaseId}`。
- 级联清理：`ProblemService.removeProblemWithTestcases` 在删除题目时会显式清理 `problem_testcase` 中的关联数据，数据库层仍保留外键 `ON DELETE CASCADE` 作为兜底。
- 测试：`ProblemControllerTest` 覆盖公开查询、测试用例 CRUD 以及删除题目后测试用例被清空的场景。

### 公告 & 题解（新增）
- 公告：学生/教师/管理员可调用 `GET /api/announcements`、`GET /api/announcements/{id}` 浏览启用公告，仅管理员可通过 `/api/admin/announcements` 创建/更新/删除并设置置顶、上下线与发布时间。
- 题解：学生/教师/管理员可使用 `GET /api/solutions`、`GET /api/problems/{problemId}/solutions`、`GET /api/solutions/{id}` 查询；学生通过 `POST /api/problems/{problemId}/solutions` 发布题解，作者或管理员 `PUT/DELETE /api/solutions/{id}` 维护，创建前校验题目存在。
- 权限：题解仅作者或管理员可改删，未启用题解对普通用户隐藏；公告写操作限定管理员。
- 测试：`AnnouncementControllerTest`、`SolutionControllerTest` 覆盖管理员 CRUD、师生只读、题解作者权限、管理员兜底删除。

### 作业 & 作业题（新增）
- 查询：`GET /api/homeworks` 支持班级过滤及启用过滤，`GET /api/homeworks/{id}`、`GET /api/homeworks/{id}/problems` 返回详情与题目列表；学生访问未启用作业将收到 404。
- 管理：教师/管理员通过 `POST /api/homeworks` 创建（必须附 `problemIds`）、`PUT /api/homeworks/{id}` 更新（可替换题目列表）、`DELETE /api/homeworks/{id}` 删除；教师只能操作自己创建的班级。`POST /api/homeworks/{id}/problems` 与 `DELETE /api/homeworks/{id}/problems/{problemId}` 维护作业题目。
- 校验：创建/更新校验班级存在、起止时间合法、题目 ID 真实存在，删除作业自动清理 `homework_problem`；题目批量新增会自动去重并忽略已存在记录。
- 测试：`HomeworkControllerTest` 覆盖教师全链路、学生只读与权限阻断。

### 讨论 & 评论（新增）
- 查询：`GET /api/discussions`、`GET /api/discussions/{id}` 面向学生/教师/管理员，支持按题目/作者筛选；学生只能看到启用讨论。
- 学生创作：`POST /api/discussions` 发表讨论，作者或管理员 `PUT/DELETE /api/discussions/{id}` 维护；管理员可切换 `isActive`。
- 评论：`GET /api/discussions/{id}/comments` 浏览、`POST /api/discussions/{id}/comments`（学生发布）、`DELETE /api/discussions/comments/{commentId}`（作者或管理员删除），当前不支持楼中楼。
- 校验：讨论/评论发布时校验题目和讨论存在性，未启用讨论对普通学生隐藏。
- 测试：`DiscussionControllerTest` 验证学生/管理员在发帖、评论与删除流程中的权限分支。

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
## 待办
- 学生头像上传：复用现有 FileController + AvatarStorageService 能力，新增一个“学生更换头像”接口即可。上传成功后把返回的 URL 写入 student.avatar 字段，必要时校验图片尺寸/格式（已有校验逻辑可以直接复用）。
- 操作留痕（管理员/教师/学生）：可以在现有 LoginLog 之外再建一张 “操作日志表”，字段覆盖 userId/role/ip/ipLocation/device/action/path/payload/时间。通过 Spring HandlerInterceptor 或 AOP 切入器统一记录（只记录需要审计的接口即可）。
  IP 地理位置可以延用登录时的 Ip2Region/IP API，设备来自请求头 UA。
- 消息通知系统：短期可以先做“站内通知表 + 轮询接口”，把重要事件（例如管理员审批、作业发布、讨论回复）写成通知，学生/教师通过 GET /api/notifications 拉取；远期可加 WebSocket/长轮询推送，也可与邮箱/短信对接。

待办（写入 ProjectDesc）：

1. 学生头像更新接口：在 FileController 中新增 PUT /api/students/me/avatar，上传后更新 student.avatar，复用头像存储/校验逻辑。
2. 操作审计日志：设计 operation_log 表及 Service，在关键 Controller 上通过拦截器记录 role/userId/ip/位置/设备/操作路径/参数摘要，并提供管理员查询接口。
3. 消息通知系统：落地站内通知表、推送规则及查询接口（后续可扩展实时推送），作为作业/讨论等模块的统一通知渠道。