# Repository Guidelines

## 项目结构与模块组织
主业务代码位于 `src/main/java`，按 `controller`、`service`、`mapper`、`entity` 等包分层，遵循 Spring Boot MVC 典型职责划分。公共配置、YAML、SQL 初始化脚本 `re-oj.sql`、邮件模版在 `src/main/resources`，若需要新增静态资源请建独立 `static/` 子目录。集成测试与 MockMvc 用例放在 `src/test/java`，命名空间镜像主代码包结构，例如 `com/oj/onlinejudge/controller`。静态文档与设计资料集中在 `Doc/`、`HELP.md` 与 `ProjectDesc.md`，而 `build/`、`bin/` 中的文件均为构建产物，不应直接修改。

## 构建、测试与开发命令
- `./gradlew clean build`：执行依赖解析、Java 8 编译、Spring Boot 可执行 jar 打包，并串行运行所有测试。
- `./gradlew test`：仅运行 JUnit 5 + Spring Boot Test 套件，使用 H2 内存库，适合增量验证。
- `./gradlew bootRun`：加载 `application.yml` 启动本地 API，可通过 `SPRING_DATASOURCE_URL` 等环境变量覆盖配置。
- `./gradlew flywayMigrate`（如需）：在数据库更新前可选执行 SQL 脚本或迁移任务，确保 schema 一致。

## 代码风格与命名约定
统一使用 4 空格缩进、花括号不换行、方法名采用动宾短语（如 `resetPasswordToken`）。包名保持小写，REST 端点采用复数资源路径（`/students`、`/classes/{id}/members`），并在 `@RequestMapping` 上声明清晰的 `produces`。DTO 与实体优先用 Lombok（`@Data`、`@Builder`），MyBatis-Plus mapper 以 `Mapper` 结尾。参数校验依赖 `javax.validation` 注解，配置常量集中在 `application.yml` 或 `@ConfigurationProperties`；若格式化需求一致，可运行 `./gradlew spotlessApply`（可选）以保持代码风格。

## 测试规范
当前测试栈包含 Spring Boot Test、MockMvc、H2 驱动。新测试文件使用 `*Test` 后缀并与被测类同路径，例如 `TeacherControllerTest`。编写断言时同时检查 HTTP 状态与 payload 字段，可借助 `@Sql` 或测试数据构造器准备持久化依赖。如果扩展 service 层，请补充 `@SpringBootTest` + `@AutoConfigureMockMvc` 或 `@DataJpaTest` 场景，并关注覆盖率报告（可通过 `./gradlew jacocoTestReport` 获取 `build/reports/jacoco/test/html/index.html`）。提交前至少执行 `./gradlew test` 并记录失败复现步骤或需要忽略的用例。

## 提交与 Pull Request 要求
Git 历史表明提交主题为简短的祈使句（中英皆可），如“添加重置密码接口”。保持 72 字符以内，必要时在正文描述设计和回滚方案。PR 应包括：变更摘要、关联 issue/需求、数据库或配置影响、截图或 API 样例（若接口变更）。提交前确认 `./gradlew test` 通过，并在说明中列出后续任务或已知风险，便于审阅者快速定位重点。

## 安全与配置提示
禁止提交真实 SMTP、Redis、MySQL 凭据；本地可以创建 `.env` 或 `application-local.yml` 并在 `.gitignore` 中维护。云端部署时建议接入 Vault、AWS Secrets Manager 或 Kubernetes ConfigMap 管理密钥。更新 `re-oj.sql` 时确保 DDL 幂等（`IF NOT EXISTS`）以便多次执行。涉及令牌、文件上传、敏感日志的逻辑应放在独立 service，并在 controller 中只做最少的数据拼装以便审计。

## 架构与运行环境
服务基于 Spring Boot 2.6.13，默认使用 MySQL + Redis，身份认证通过 JWT（`io.jsonwebtoken` 组件）与邮箱验证码协同。开发时建议启用 profile `local`，通过 `SPRING_PROFILES_ACTIVE=local` 或 `--spring.profiles.active=local` 来隔离外部依赖。Redis、SMTP、数据库均可利用 `docker compose` 启动临时容器，方便快速复现线上状态。若引入新中间件，请在 `Doc/` 中补充分层图与时序描述，并在 `application.yml` 注释默认端口和示例凭据，保证其他贡献者能在 5 分钟内完成环境拉起。
