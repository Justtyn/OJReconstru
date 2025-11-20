const TOKEN_KEY = 'oj_token';
const THEME_KEY = 'oj_theme_mode';

const isBrowser = () => typeof window !== 'undefined';

export const storage = {
  getToken() {
    if (!isBrowser()) return '';
    return localStorage.getItem(TOKEN_KEY) ?? '';
  },
  setToken(token: string) {
    if (!isBrowser()) return;
    localStorage.setItem(TOKEN_KEY, token);
  },
  clearToken() {
    if (!isBrowser()) return;
    localStorage.removeItem(TOKEN_KEY);
  },
  getThemeMode() {
    if (!isBrowser()) return null;
    return localStorage.getItem(THEME_KEY) as 'light' | 'dark' | 'system' | null;
  },
  setThemeMode(mode: string) {
    if (!isBrowser()) return;
    localStorage.setItem(THEME_KEY, mode);
  },
};
