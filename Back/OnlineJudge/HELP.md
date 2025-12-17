# OnlineJudge 后端运行与验收指南

本文档面向本科毕业设计场景：快速把后端跑起来，并完成基本验收（含 Swagger 调试）。

## 1. 环境要求

- JDK：Java 8
- 构建提示：如遇到 Lombok 编译报错（例如 `TypeTag :: UNKNOWN`），请将 Gradle JVM/`JAVA_HOME` 切换到 JDK 17/18 再执行构建。
- 数据库：MySQL（本项目默认库名 `re-oj`）
- 缓存：Redis（`application.yml` 默认开启注册/验证码 Redis 存储）
- 构建工具：使用 Gradle Wrapper（无需本机安装 Gradle）

## 2. 初始化数据库

1) 创建数据库（示例）：

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS \`re-oj\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

2) 导入表结构与初始数据：

- SQL 文件：`src/main/resources/re-oj.sql`
- 注意：该 SQL 含 `DROP TABLE IF EXISTS`，请在**空库/新库**上执行。

```bash
mysql -u root -p re-oj < src/main/resources/re-oj.sql
```

## 3. 启动后端服务

1) 确保 MySQL / Redis 已启动，并与配置一致。

2) 启动：

```bash
./gradlew bootRun
```

默认端口：`8080`  
接口前缀：`/api/**`（例如：`/api/auth/login`）

## 4. 运行测试（验收建议项）

测试使用 H2 内存库与测试配置：`src/test/resources/application-test.yml`。

```bash
./gradlew test
```

## 5. Swagger / OpenAPI 调试步骤

1) 启动项目后打开 Swagger UI：

- `http://localhost:8080/swagger-ui/index.html`

2) 获取 Token：

- 在 Swagger 中调用 `POST /api/auth/login`
- 响应体 `data.token` 通常为 `Bearer <JWT>`

3) 认证：

- Swagger 右上角点击 “Authorize”
- 通常只需要粘贴**纯 JWT**（若返回值包含 `Bearer ` 前缀，请去掉前缀再粘贴）

4) 调用需要登录的接口（例如提交、查询个人信息等），Swagger 会自动携带 `Authorization` 请求头。

OpenAPI JSON：

- `http://localhost:8080/v3/api-docs`

## 6. 常见问题（快速排查）

- 启动报数据库连接失败：检查 `application.yml` 的 `spring.datasource.*` 与本机 MySQL 账号/端口/库名是否一致。
- 启动报 Redis 连接失败：确认本机 Redis 已启动；或临时将 `app.auth.verify.store` 调整为 `memory`（仅本地调试用）。
- Swagger 能打开但接口 401：确认已 Authorize，且粘贴的是 JWT（不要重复带 `Bearer ` 前缀）。
- 判题相关接口报错：检查 `application.yml` 的 `judge0.base-url` 指向的 Judge0 服务是否可用。
