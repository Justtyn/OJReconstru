# Repository Guidelines

## Project Structure & Module Organization
Source lives under `src/main/java/com/oj/onlinejudge`, separated by layers (`controller`, `service`, `mapper`, `domain/entity`). DTOs and validation groups reside in `src/main/java/com/oj/onlinejudge/domain/dto`, mirroring the modules they serve. Configuration, SQL bootstrap (`re-oj.sql`), and YAML files sit in `src/main/resources`. Tests mirror the main tree inside `src/test/java`, e.g., `com/oj/onlinejudge/controller/*Test`. Docs and specs are kept in `Doc/`, `ProjectDesc.md`, and `HELP.md`. Generated artifacts (`build/`, `bin/`) should never be edited manually; uploaded avatars land in `uploads/` and are served via `/files/avatars/`.

## Build, Test, and Development Commands
- `./gradlew clean build`: resolve dependencies, compile Java 8 sources, run tests, package the Boot jar.
- `./gradlew test`: run Spring Boot + MockMvc suites with the H2 profile; CI replica.
- `./gradlew bootRun`: launch the API using `application.yml`; override via `SPRING_*` env vars.
- `./gradlew jacocoTestReport`: generate coverage at `build/reports/jacoco/test/html/index.html`.
Use the Gradle wrapper to keep plugin/tooling versions in sync.

## Coding Style & Naming Conventions
Follow 4-space indentation, K&R braces, and descriptive method names (`sendVerificationCode`). REST endpoints use plural nouns (`/students`, `/classes-members`). DTOs employ Lombok (`@Data`) and `javax.validation` annotations, grouped via `CreateGroup`/`UpdateGroup`. Mapper interfaces end with `Mapper`; services extend MyBatis-Plus `ServiceImpl`. Externalized config belongs in YAML or `@ConfigurationProperties`; avoid literals in controllers.

## Testing Guidelines
Testing stack: Spring Boot Test, MockMvc, H2. Every controller has a matching `*ControllerTest` verifying HTTP status and payload (`jsonPath`). When persistence behavior matters, seed data via `schema-test.sql` / `data-test.sql` or `@Sql`. Breakage reproduction steps belong in PR descriptions. Target: run `./gradlew test` locally before committing; inspect Jacoco report for coverage hot spots.

## Commit & Pull Request Guidelines
Commits use short, imperative subjects (English or Chinese) typical of `git log` (e.g., “添加重置密码接口”). Keep body paragraphs for rationale/rollback notes. PRs should provide: summary of changes, linked issue/requirement, screenshots or API samples for user-facing updates, DB/config impacts, and verification steps (`./gradlew test`). Call out follow-up tasks so reviewers understand scope. Avoid committing secrets; rely on environment variables or ignored `application-local.yml` instead.

## Security & Configuration Tips
Mask real SMTP/Redis/MySQL credentials; production deploys should use Vault/Secrets Manager/ConfigMap. Ensure SQL DDL is idempotent (`IF NOT EXISTS`) so `re-oj.sql` can be rerun. Sensitive flows (token issuance, file upload) belong in dedicated services with audit-friendly logs. The avatar upload module enforces square images and serves them via `/files/avatars/**`; adjust `app.storage` in `application.yml` when relocating storage.
