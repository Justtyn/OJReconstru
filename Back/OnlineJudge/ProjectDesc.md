# Re-OnlineJudge Backend · 开发日志与项目说明

## 概览
Spring Boot 2.6 + MyBatis-Plus 实现的在线判题/教学教务后端，当前提供用户体系、班级、日志、邮件、验证码等能力。数据源为 MySQL，测试场景使用 H2；认证基于 JWT，`ApiResponse{code,message,data}` 为统一返回结构。`re-oj.sql` 与 `schema-test.sql` 同步维护表结构，确保开发/测试一致。

## 近期迭代
- **2025-11-16**：新增本地头像上传能力（正方形图片校验、静态资源映射）、补充 `app.storage` 配置、完善测试覆盖；同步 H2 Schema 至最新数据库结构。
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
- 班级与班级成员：`/api/classes`、`/api/classes-members`
- 数据脚手架：`re-oj.sql` 含 announcement、discussion、homework、problem、submission 等新表，实体位于 `domain/entity`，待补业务接口。

### 文件服务（新增）
- 接口：`POST /api/files/avatar`（需登录，`multipart/form-data`，字段 `file`）
- 规则：只接受 `png/jpg/jpeg/gif/webp`，必须为正方形图片；使用 `UUID` 命名，保存于 `app.storage.avatar-dir`（默认 `./uploads/avatars`）
- 访问：上传成功返回 `{"url":"/files/avatars/<name>.png"}`，通过 Spring MVC 静态资源映射直接访问
- 校验：读取图片像素确认正方形，非图片或非正方形会返回 `400` 与失败原因

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

## 测试
- `./gradlew test`：H2 内存库 + MockMvc 覆盖认证/用户/头像上传用例
- `FileControllerTest` 新增两个场景：上传正方形图片成功、非正方形返回 400，运行后可在 `build/test-avatars` 观察落盘文件
- 生成报告：`build/reports/tests/test/index.html`

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
| 日志 | `GET /api/login-logs` | 支持按角色/用户过滤 |
| 文件 | `POST /api/files/avatar` | 上传正方形头像，返回 URL |

## 开发建议
1. **新模块脚手架**：实体已就绪，后续添加 Mapper → Service → Controller → Test 即可；推荐通过 MP Generator 批量生成（可扩展 `AvatarStorageService` 的写法）。
2. **数据库迁移**：长期建议引入 Flyway，将 `re-oj.sql` 拆分为版本化脚本；短期内修改结构请同步 `schema-test.sql`。
3. **文件扩展**：若需公告/讨论贴图，可复用 `AvatarStorageService` 思路，抽象出 `FileCategory`，并对上传类型/目录做差异化配置。
4. **安全**：虽然头像上传暂未限速，仍建议部署层添加基本防护（大小限制、WAF）；JWT 秘钥、邮件授权码等敏感配置请通过环境变量注入。

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
