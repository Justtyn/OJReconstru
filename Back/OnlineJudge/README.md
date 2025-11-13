# Re-OnlineJudge Backend

Spring Boot 后端服务，提供在线判题平台的用户认证与基础教务能力（学生/教师/管理员、班级与成员管理、登录日志、邮件通知等）。

- 运行环境：Java 8+, Spring Boot 2.6.x
- 存储组件：MySQL、Redis
- 依赖组件：MyBatis-Plus、PageHelper、Spring Mail、JJWT、Springdoc OpenAPI

## 当前功能概览

认证与安全
- 注册（支持邮箱验证码）/ 验证邮箱 / 登录 / 注销 / 当前用户信息
- 角色：student | teacher | admin（登录时可指定）
- JWT 鉴权过滤器（Authorization: Bearer <token>），白名单：/api/auth/login, /api/auth/register, /api/auth/verifyEmail
- 登录日志记录（成功/失败、时间、IP、UA、设备），支持异步
- 邮件通知（注册成功、登录提醒、登出提醒）

用户与教务
- 学生 Student：分页、详情、创建、更新、删除（创建/更新时自动对明文密码进行哈希）
- 教师 Teacher：分页、详情、创建、更新、删除
- 管理员 Admin：分页、详情、创建、更新、删除
- 班级 Classes：分页、详情、创建、更新、删除
- 班级成员 ClassesMember：分页（可按 classId 过滤）、详情、创建、更新、删除

可视化与文档
- OpenAPI/Swagger 文档（集成 BearerAuth），便于在 UI 中携带 Token 调试

## 架构与技术栈

- Spring Boot Web/Validation/Data Redis/Mail
- MyBatis-Plus + PageHelper（分页）
- JJWT 0.11.x（HS256 签名）
- Springdoc OpenAPI 1.6.x
- Lombok（编译期注解）

## 配置说明（默认值见 `src/main/resources/application.yml`）

数据源与 Redis（默认本地）
- spring.datasource.url=jdbc:mysql://localhost:3306/re-oj
- spring.datasource.username=root
- spring.datasource.password=你的密码
- spring.redis.host=localhost
- spring.redis.port=6379

JWT
- jwt.header=Authorization
- jwt.prefix=Bearer
- jwt.secret=请使用足够长度的随机字符串
- jwt.expire-minutes=120
- jwt.whitelist=[/api/auth/login, /api/auth/register, /api/auth/verifyEmail]

认证与邮件
- app.auth.require-email-verify=true（开启注册邮箱验证）
- app.auth.verify.store=redis | memory（验证码与待注册信息存储方式）
- app.auth.verify-redis-prefix=verify:reg:
- app.mail.enabled=true（启用邮件发送）
- spring.mail.*（SMTP 配置，示例：QQ 邮箱 host=smtp.qq.com, 端口 587，password 为授权码）

安全提醒
- 不要将真实数据库口令、邮件账号授权码提交到公共仓库。推荐用环境变量覆盖。

## 快速开始（macOS / zsh）

前置要求
- JDK 8+
- MySQL（本地有库 re-oj）
- Redis（本地 6379 端口）
- 可用的 SMTP 邮箱（如 QQ 邮箱授权码）

1) 准备 Redis（二选一）
```zsh
# Homebrew
brew install redis
brew services start redis
redis-cli ping

# 或 Docker
# docker run -d --name redis -p 6379:6379 redis:7-alpine
```

2) 准备数据库
- 创建数据库 re-oj，并导入初始化脚本：`src/main/resources/re-oj.sql`
- application.yml 默认 `spring.sql.init.mode=never`，因此需要手工导入一次

3) 配置 SMTP（建议使用环境变量覆盖）
```zsh
export SPRING_MAIL_HOST=smtp.qq.com
export SPRING_MAIL_PORT=587
export SPRING_MAIL_USERNAME=你的邮箱@qq.com
export SPRING_MAIL_PASSWORD=你的邮箱授权码
export APP_MAIL_ENABLED=true
export APP_AUTH_REQUIRE_EMAIL_VERIFY=true
export APP_AUTH_VERIFY_STORE=redis
```

4) 启动应用
```zsh
./gradlew bootRun
```
- 默认端口：http://localhost:8080
- API 文档（其一）：
  - http://localhost:8080/swagger-ui.html
  - http://localhost:8080/swagger-ui/index.html

## 常用接口示例（curl）

注册（发送验证码）
```zsh
curl -X POST http://localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"P@ssw0rd","email":"your@qq.com","name":"Alice"}'
```

若未收到邮件，可从 Redis 读取验证码（默认 key：`verify:reg:<username>`）
```zsh
redis-cli GET "verify:reg:alice"
```

验证邮箱
```zsh
curl -X POST http://localhost:8080/api/auth/verifyEmail \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","code":"上一步拿到的验证码"}'
```

登录（学生为默认角色）
```zsh
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"P@ssw0rd"}'
```

携带 Token 访问受保护接口
```zsh
TOKEN="<登录返回的token（不含前缀）>"
curl http://localhost:8080/api/auth/users/me \
  -H "Authorization: Bearer ${TOKEN}"
```

注销
```zsh
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer ${TOKEN}"
```

## 开发与测试

构建与测试
```zsh
./gradlew clean test
```
- 单元测试使用 H2 内存库，已在 `src/test/resources/application-test.yml` 中关闭邮件、关闭邮箱验证，并切换验证码存储为 memory，便于 CI。

## 故障排查（Troubleshooting）

1) 415 / HttpMediaTypeNotSupportedException（或类似）
- 原因：请求头 Content-Type 非 `application/json`，或请求体不是合法 JSON。
- 解决：为所有 @RequestBody 的接口添加 `-H 'Content-Type: application/json'` 并传入 JSON 字符串。

2) 邮件发送失败
- 检查 spring.mail.host/port/username/password 是否正确；QQ 邮箱需使用授权码。
- 如仅本地联调，可先关闭发信：`app.mail.enabled=false`，再从 Redis 读取验证码。

3) Redis 连接失败
- 确认 redis 服务已启动并监听 6379；本机可 `redis-cli ping`。
- 如无 Redis，可临时切换：`app.auth.verify.store=memory`。

4) JWT 401 未授权
- 确保请求头携带：`Authorization: Bearer <token>`。
- 白名单仅覆盖登录/注册/验证邮箱接口。

5) 数据库表缺失或外键错误
- 确保已在 MySQL 中导入 `src/main/resources/re-oj.sql`。

## 路线与进展

- 已完成：注册+邮箱验证、登录/登出、JWT 鉴权过滤器、登录日志、邮件通知、学生/教师/管理员/班级/班级成员 CRUD、OpenAPI 文档。
- 测试：已有控制器层测试（见 build/reports/tests/test），本地测试通过。
- 下一步建议：
  - 增加更多领域模型（题库、评测任务、提交记录等）与权限策略
  - 集成对象存储（头像/附件等）与操作审计
  - 对接口增加更细粒度的鉴权与限流

## 目录结构（简述）

- src/main/java/com/oj/onlinejudge
  - controller：REST 控制器（auth、student、teacher、admin、classes、classes-member...）
  - security：JWT、过滤器、密码服务
  - service / impl：业务服务、验证码存储（memory/redis）、邮件服务
  - config：JWT/过滤器/OpenAPI/异步/数据源/跨域等配置
  - domain：entity/dto/vo
- src/main/resources
  - application.yml（生产/开发默认）
  - re-oj.sql（初始化 SQL）
- src/test/resources
  - application-test.yml（测试配置）

---
如需我根据你的实际部署环境（端口、域名、邮箱提供商）定制一版 README 或添加 Docker Compose（MySQL+Redis+App），告诉我即可。
