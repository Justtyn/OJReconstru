# GEMINI.md - Project Context

## Project Overview
`oj-vue` is a Vue 3 frontend application for an Online Judge (OJ) system. It uses TypeScript and Vite for a modern development experience and Ant Design Vue for the UI component library.

## Technology Stack
*   **Framework:** Vue 3 (Composition API, `<script setup>`)
*   **Build Tool:** Vite
*   **Language:** TypeScript
*   **State Management:** Pinia
*   **Routing:** Vue Router
*   **UI Library:** Ant Design Vue
*   **HTTP Client:** Axios
*   **CSS Preprocessor:** Less
*   **Utilities:** `date-fns`, `marked` (Markdown rendering), `echarts` (Charts)

## Architecture
The project follows a specific directory structure to separate user-facing applications from administrative management modules.

### Directory Structure
*   **`src/apps/`**: Contains the main logical "applications" or views.
    *   `client/`: The student/public-facing interface (Home, Problems, Ranking, etc.).
    *   `auth/`: Authentication pages (Login, Register, Forgot Password).
    *   `admin/`: The admin dashboard shell.
*   **`src/modules/`**: Contains the **Admin/Teacher management** feature implementations.
    *   Each module (e.g., `problem`, `student`, `class`) contains its own `pages/` and `components/`.
    *   These are referenced by the Admin Router.
*   **`src/services/`**: API service layer.
    *   `http.ts`: Configured Axios instance with interceptors for JWT injection and error handling.
    *   `modules/`: Feature-specific API calls.
*   **`src/stores/`**: Pinia stores (e.g., `auth.ts` for user session).
*   **`src/router/`**: Routing configuration.
    *   `modules/client.ts`: Routes for the student interface.
    *   `modules/admin.ts`: Routes for the admin interface (referencing `src/modules`).
*   **`src/types/`**: TypeScript interfaces and DTOs.

### Key Files
*   `src/main.ts`: Application entry point. Sets up Ant Design, Router, and Pinia.
*   `src/services/http.ts`: Axios configuration. Handles API base URL, request headers (Authorization), and response unwrapping.
*   `vite.config.ts`: Vite configuration, defines the `@` alias for `src`.

## Building and Running
*   **Development Server:** `npm run dev` (starts Vite dev server)
*   **Build for Production:** `npm run build` (runs `vue-tsc` type check then `vite build`)
*   **Preview Production Build:** `npm run preview`

## Development Conventions
1.  **Routing:**
    *   New user-facing pages go in `src/apps/client` and `src/router/modules/client.ts`.
    *   New admin management pages go in `src/modules/<feature>` and `src/router/modules/admin.ts`.
2.  **API Handling:**
    *   Backend responses are expected in the format `{ code: 0, data: ... }`.
    *   Non-zero codes are rejected by the interceptor.
    *   Auth token is automatically injected if present in the Auth Store.
3.  **Styling:**
    *   Use Less for custom styles.
    *   Ant Design Vue is used for the component system; prefer its components over custom implementations where possible.
