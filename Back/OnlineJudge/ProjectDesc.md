# Re-OnlineJudge Backend

一个基于 Spring Boot 的在线判题平台后端基础服务，当前聚焦于通用用户体系与教学教务域：注册 + 邮箱验证、登录 / 注销、角色管理（学生 / 教师 / 管理员）、班级与班级成员、登录日志、邮件通知、统一认证与分页查询能力。

---
## 特性速览

身份认证与安全
- 支持注册（可选邮箱验证码激活）、邮箱验证、登录、注销、获取当前用户
- 角色：`student | teacher | admin` 登录时可显式指定，默认 student
- JWT 鉴权：HS256 签名（JJWT 0.11.x），请求头 `Authorization: Bearer <token>`；统一白名单路径配置
- 登录日志：记录成功/失败、IP、UA、设备、时间，可分页筛选
- 邮件通知：注册成功 / 登录提醒 / 注销提醒 / 邮箱验证码
- 统一密码服务：BCrypt + 全局盐值（避免简单彩虹表）

教务与基础数据
- 学生 / 教师 / 管理员 CRUD（创建/更新时自动对明文密码加密）
- 班级 Classes 与班级成员 ClassesMember CRUD，支持按 `classId` 过滤
- 分页查询：MyBatis-Plus + PageHelper（向后兼容）

开发体验与运维
- OpenAPI / Swagger UI（已集成 Bearer 鉴权按钮）
- 统一响应结构 `ApiResponse`：`{ code, message, data }`，`code=0` 表示成功
- 全局异常处理：参数校验 / 业务异常 / 未知异常 -> 标准 JSON 响应
- 可通过 Redis 或内存存储待验证注册信息（验证码）
- 测试环境使用 H2 内存库 + 关闭邮件与验证码要求，便于快速 CI

---
## 技术栈

后端框架：Spring Boot 2.6.13
语言与环境：Java 8+（sourceCompatibility=1.8）
持久化：MyBatis-Plus 3.5.x、PageHelper、MySQL 8+（可兼容 5.7）、H2（测试）
缓存 & 临时存储：Redis 7+（验证码、可扩展会话/限流）
认证与安全：JJWT、BCrypt（Spring Security Crypto）
文档：springdoc-openapi-ui 1.6.x
邮件：Spring Mail（SMTP）
工具：Lombok（编译期）、Gradle Wrapper

---
## 目录结构（简述）

```
src/main/java/com/oj/onlinejudge
  ├── controller        # REST 控制器（auth、student、teacher、admin、classes、classes-member、login-log）
  ├── domain            # entity / dto / vo
  ├── service (+impl)   # 业务服务、验证码存储、邮件发送、日志
  ├── security          # JWT、过滤器、密码服务、已认证用户对象
  ├── config            # CORS、Async、JWT、OpenAPI、Swagger、MyBatis-Plus 等配置
  ├── common/api        # 统一响应模型 ApiResponse
  ├── exception         # 全局异常处理 (GlobalExceptionHandler)
src/main/resources
  ├── application.yml   # 主配置
  ├── re-oj.sql         # 初始化建表脚本
src/test/resources
  ├── application-test.yml  # 测试配置（H2 / 禁用邮件 / 内存验证码）
```

---
## 配置说明（节选）

见 `src/main/resources/application.yml`：

### MySQL
```
spring.datasource.url=jdbc:mysql://localhost:3306/re-oj
spring.datasource.username=root
spring.datasource.password=<你的密码>
```

### Redis
```
spring.redis.host=localhost
spring.redis.port=6379
```

### JWT
```
jwt.header=Authorization
jwt.prefix=Bearer
jwt.secret=<一段足够长的随机字符串>
jwt.expire-minutes=120
jwt.whitelist:
  - /api/auth/login
  - /api/auth/register
  - /api/auth/verifyEmail
```

### 验证码与注册
```
app.auth.require-email-verify=true   # 是否启用邮箱验证码激活
app.auth.verify.store=redis          # redis | memory
app.auth.verify-redis-prefix=verify:reg:
```

### 邮件
```
app.mail.enabled=true
spring.mail.host=smtp.qq.com
spring.mail.port=587
spring.mail.username=<邮箱>@qq.com
spring.mail.password=<授权码>
```

### 密码盐值
```
security.password.salt=change-this-salt
```
生产环境请通过环境变量或外部配置中心覆盖敏感信息，避免硬编码。

---
## 响应结构

统一使用：
```
{
  "code": 0,
  "message": "success",
  "data": { ... }
}
```
失败示例：
```
{
  "code": 400,
  "message": "参数不合法",
  "data": null
}
```
JWT 失败（401）：
```
{
  "code": 401,
  "message": "Token非法或已过期",
  "data": null
}
```

---
## 快速启动（macOS / zsh）

前置要求：已安装 JDK 8+、Gradle Wrapper（仓库已附）、MySQL、Redis、可用 SMTP 邮箱。

1. 安装 / 启动 Redis：（二选一）
```zsh
brew install redis
brew services start redis
redis-cli ping
# 或 Docker
# docker run -d --name redis -p 6379:6379 redis:7-alpine
```
2. 初始化数据库：创建库 `re-oj` 并导入 `src/main/resources/re-oj.sql`
3. 配置环境变量（推荐替换 yml 中敏感项）：
```zsh
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/re-oj?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false"
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=<你的MySQL密码>
export SPRING_MAIL_HOST=smtp.qq.com
export SPRING_MAIL_PORT=587
export SPRING_MAIL_USERNAME=<邮箱>@qq.com
export SPRING_MAIL_PASSWORD=<授权码>
export APP_MAIL_ENABLED=true
export APP_AUTH_REQUIRE_EMAIL_VERIFY=true
export APP_AUTH_VERIFY_STORE=redis
export SECURITY_PASSWORD_SALT="change-this-salt"
export JWT_SECRET="<至少32位随机字符串>"
```
4. 启动：
```zsh
./gradlew bootRun
```
访问：`http://localhost:8080`
Swagger UI：
- `http://localhost:8080/swagger-ui.html`
- or `http://localhost:8080/swagger-ui/index.html`
点击右上角 Authorize，填入：`Bearer <token>` 或只填原始 token（前缀由界面自动添加）。

---
## Docker Compose（可选快速栈）

示例（自行在根目录创建 `docker-compose.yml`）：
```
version: '3.9'
services:
  mysql:
    image: mysql:8.0
    container_name: reoj-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: re-oj
    command: ["--default-authentication-plugin=mysql_native_password"]
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./src/main/resources/re-oj.sql:/docker-entrypoint-initdb.d/re-oj.sql
  redis:
    image: redis:7-alpine
    container_name: reoj-redis
    ports:
      - "6379:6379"
  app:
    build: .
    container_name: reoj-backend
    depends_on:
      - mysql
      - redis
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/re-oj?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root
      SPRING_REDIS_HOST: redis
      APP_AUTH_VERIFY_STORE: redis
      APP_MAIL_ENABLED: "false"   # 初期可关闭邮件
      JWT_SECRET: <请替换>
      SECURITY_PASSWORD_SALT: <请替换>
    ports:
      - "8080:8080"
volumes:
  mysql_data:
```
然后：
```zsh
docker compose up -d --build
```

---
## 常用接口（curl 示例）

注册（发送验证码或直接注册，取决于配置）
```zsh
curl -X POST http://localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"P@ssw0rd","email":"alice@qq.com","name":"Alice"}'
```
读取验证码（Redis 模式）：
```zsh
redis-cli GET "verify:reg:alice"
```
邮箱激活：
```zsh
curl -X POST http://localhost:8080/api/auth/verifyEmail \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","code":"123456"}'
```
登录（默认 student）：
```zsh
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"P@ssw0rd"}'
```
携带 Token 获取当前用户：
```zsh
TOKEN=<登录返回的token(不含Bearer)>
curl http://localhost:8080/api/auth/users/me -H "Authorization: Bearer ${TOKEN}"
```
注销：
```zsh
curl -X POST http://localhost:8080/api/auth/logout -H "Authorization: Bearer ${TOKEN}"
```
分页查询学生：
```zsh
curl "http://localhost:8080/api/students?page=1&size=10" -H "Authorization: Bearer ${TOKEN}"
```

---
## 认证流程简述
1. 客户端调用注册：
   - 如启用邮箱验证：生成验证码 + Redis 暂存 -> 发邮件 -> 返回“等待验证”
   - 如未启用：直接创建用户并返回 Token
2. 客户端提交验证码（启用时）：校验成功 -> 创建用户 -> 返回 Token
3. 登录：账号密码 + 角色（可选） -> 验证密码 -> 生成 JWT -> 返回
4. 后续请求：在请求头携带 `Authorization: Bearer <token>` -> 过滤器解析有效性 -> 在 `request.getAttribute("authenticatedUser")` 中放入用户对象
5. 注销：记录登出时间，可选邮件提醒

JWT 负载字段：`sub` (userId), `username`, `role`, `iat`, `exp`

---
## 测试与构建

运行全部测试：
```zsh
./gradlew clean test
```
测试特点：
- 使用 H2 内存数据库（避免对真实 MySQL 写入）
- 关闭邮件发送 `app.mail.enabled=false`
- 验证码存储切换为 memory（无需 Redis）
- 生成的测试报告：`build/reports/tests/test/index.html`

---
## 故障排查 (Troubleshooting)

1. 415 / MediaType 错误：缺少 `Content-Type: application/json` 或 JSON 语法错误
2. 邮件未发送：确认 SMTP 主机、端口、授权码；可临时关闭邮件功能 `app.mail.enabled=false`
3. Redis 连接失败：确认服务已启动；可改用 `app.auth.verify.store=memory`
4. 401 未授权：检查 Token 是否携带、是否过期、前缀是否正确
5. 表缺失或外键错误：是否已导入 `re-oj.sql`
6. 密码不匹配：是否使用了创建后返回的加密密码；更新时未传 password 会保留旧值

---
## 安全建议
- 勿在公共仓库提交真实密码、JWT 密钥、邮件授权码
- 更换默认盐值：`security.password.salt`
- 生产环境使用 HTTPS，前置网关可做限流 / 防护
- 考虑为登录增加频率限制与 IP 黑名单策略

---
## 后续规划（Roadmap）
- 题目 / 题库 / 提交记录 / 判题任务 模块
- 代码沙箱集成（安全隔离执行用户代码）
- 排名 / 统计 / 数据看板
- 更细颗粒度权限（RBAC 或 ABAC）
- 分布式会话与刷新 Token 机制
- 操作审计与安全告警
- Docker 镜像与 CI/CD 流程完善

---
## 贡献指南（初稿）
1. Fork & Clone
2. 创建功能分支：`git checkout -b feature/<name>`
3. 编码规范：保持统一响应结构；新增公共异常请在 `GlobalExceptionHandler` 映射
4. 添加/更新测试：控制器新增接口务必补充对应测试类
5. 提交信息：语义化（feat/fix/docs/refactor/test/chore）
6. PR 描述：说明动机、变更点、回归影响、测试覆盖情况

---
## 快速自检清单
- [ ] 应用可启动：`./gradlew bootRun`
- [ ] Swagger UI 可访问并能 Authorize 成功
- [ ] 注册 + 验证码（或免验证模式）成功获取 Token
- [ ] 至少一个受保护接口返回 200（携带 Token）
- [ ] MySQL 表结构与实体字段一致，无运行期映射错误
- [ ] Redis 验证码写入与读取正常（启用时）
- [ ] 测试通过：`./gradlew test` code=0

---
## License
（未声明，可根据需要添加。例如：MIT / Apache-2.0）

---
如需：自动化部署脚本、更多 CI 示例（GitHub Actions）、或判题执行沙箱的初步设计文档，可继续提出需求，我会补充。
