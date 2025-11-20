# Repository Guidelines

## Project Structure & Module Organization

- `src/` hosts the Vue 3 application code (components, router, Pinia stores, services, utils). Follow domain-driven folders (auth, classes, problems, etc.) and keep shared widgets in `src/components/common`.
- `public/` contains static assets copied as-is to the final build; place favicons or mock JSON here.
- `doc/` stores product references such as API specs and `dev-log.md`; update the log whenever you finish a milestone.
- Root-level config (`vite.config.ts`, `tsconfig*.json`, `package.json`) defines build tooling. Avoid scattering configuration in feature folders.

## Build, Test, and Development Commands

```bash
npm install        # install dependencies
npm run dev        # start Vite dev server with HMR
npm run build      # type-check via vue-tsc then bundle for production
npm run preview    # locally preview the production build
```

Run commands from the repository root. Use `.env` files for API hosts instead of editing scripts.

## Coding Style & Naming Conventions

- Use TypeScript with Vue SFCs (`<script setup lang="ts">` preferred). Indentation: 2 spaces; keep components self-contained with PascalCase filenames (e.g., `ProblemList.vue`).
- Centralize HTTP logic in `src/services` with Axios wrappers; types live under `src/types`.
- Reuse Ant Design Vue components; register global styles in `src/styles`. Document any global mixins/helpers.
- Before committing, ensure imports are sorted logically and unused symbols removed.

## Testing Guidelines

- Automated tests are not yet configured. When adding tests, place them adjacent to components (`ComponentName.spec.ts`) and document the runner in this file.
- For manual verification, capture primary flows (login, protected routes, submission). Note uncovered areas in `doc/dev-log.md`.

## Commit & Pull Request Guidelines

- Commits should pair an imperative subject with concise body details, e.g., `feat(auth): wire login form` or `fix(submission): guard empty code`.
- Reference related issues in the commit body or PR description. Each PR should include: summary of changes, testing evidence (commands/screenshots), and any follow-up TODOs.
- Keep PRs scoped by module; update `doc/dev-log.md` in the same PR so reviewers can trace progress.

## Security & Configuration Tips

- Never hardcode API URLs or secrets. Use Vite env variables (`.env`, `.env.production`) and document required keys.
- Treat tokens securely: store only in Pinia + `localStorage` where necessary, and wipe both on logout.

## Coding Style & Naming Conventions

- Use TypeScript with Vue SFCs (`<script setup lang="ts">` preferred). Indentation: 2 spaces; keep components self-contained with PascalCase filenames (e.g., `ProblemList.vue`).
- Centralize HTTP logic in `src/services` with Axios wrappers; types live under `src/types`.
- Reuse Ant Design Vue components; register global styles in `src/styles`. Document any global mixins/helpers.
- Before committing, ensure imports are sorted logically and unused symbols removed.

## UI/UX, Layout, and Theming

- Maintain a consistent, clean, and modern visual system across all pages (client-side and admin-side). Follow a unified spacing, color, and typographic rhythm.
- Favor Ant Design Vue layout primitives such as `Layout`, `Menu`, `Breadcrumb`, and `Card` to ensure unified structure across modules.
- Define global style tokens (spacing, border-radius, shadow depth, typography scale) within `src/styles` or a theme config. Avoid hardcoded values in individual components.
- Implement **light/dark mode** using Ant Design Vue’s `ConfigProvider` and theme tokens:
    - Ensure all UI elements (text, icons, buttons, cards) preserve contrast ratios in both modes.
    - Store the user’s preference in Pinia + `localStorage`, with optional support for system preference (`prefers-color-scheme`).
- Validate new views for responsiveness:
    - Ensure layout behaves properly across laptop, tablet, and reduced-width screens.
    - Highlight primary actions visually; keep secondary actions distinct but less prominent.
    - Design proper empty states, skeleton/loading states, and error states rather than leaving plain text placeholders.

## Testing Guidelines

- Automated tests are not yet configured. When adding tests, place them adjacent to components (`ComponentName.spec.ts`) and document the runner in this file.
- For manual verification:
    - Record core flows (login, navigating protected routes, submission, form validation).
    - Document gaps or edge-case behaviors in `doc/dev-log.md`.

## Commit & Pull Request Guidelines

- Commit messages should use an imperative subject line with context, e.g.:
    - `feat(auth): wire login form`
    - `fix(submission): guard empty code`
- Reference relevant issues in commit bodies or PR descriptions.
- Each PR must include:
    - Summary of changes
    - Testing evidence (commands, logs, screenshots)
    - Follow-up tasks or TODO items
- Scope each PR to a single feature/module, and update `doc/dev-log.md` in the same PR.

## Security & Configuration Tips

- Do not hardcode API URLs, keys, or tokens. Place them in `.env`, `.env.development`, `.env.production`, etc.
- The authentication token must be handled securely:
    - Store in Pinia and sync to `localStorage` only when necessary.
    - On logout, clear both store and storage immediately.
- Keep sensitive logic (user roles, permission checks) centralized in router guards or dedicated service utilities.