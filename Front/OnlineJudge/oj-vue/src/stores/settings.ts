import { defineStore } from 'pinia';
import { theme } from 'ant-design-vue';
import type { ThemeMode } from '@/types/settings';
import { storage } from '@/utils/storage';

interface SettingsState {
  themeMode: ThemeMode;
  systemPrefersDark: boolean;
  initialized: boolean;
}

let removeListener: (() => void) | null = null;

export const useSettingsStore = defineStore('settings', {
  state: (): SettingsState => ({
    themeMode: storage.getThemeMode() ?? 'system',
    systemPrefersDark: false,
    initialized: false,
  }),
  getters: {
    effectiveTheme(state) {
      if (state.themeMode === 'system') {
        return state.systemPrefersDark ? 'dark' : 'light';
      }
      return state.themeMode;
    },
    antdTheme(state) {
      return {
        algorithm: state.effectiveTheme === 'dark' ? theme.darkAlgorithm : theme.defaultAlgorithm,
        token: {
          colorBgBase: state.effectiveTheme === 'dark' ? '#0f172a' : '#ffffff',
        },
      };
    },
  },
  actions: {
    init() {
      if (this.initialized || typeof window === 'undefined') return;
      const media = window.matchMedia('(prefers-color-scheme: dark)');
      this.systemPrefersDark = media.matches;
      const listener = (event: MediaQueryListEvent) => {
        this.systemPrefersDark = event.matches;
      };
      media.addEventListener('change', listener);
      removeListener = () => media.removeEventListener('change', listener);
      this.initialized = true;
      document.documentElement.dataset.theme = this.effectiveTheme;
    },
    setTheme(mode: ThemeMode) {
      this.themeMode = mode;
      storage.setThemeMode(mode);
      document.documentElement.dataset.theme = this.effectiveTheme;
    },
    teardown() {
      removeListener?.();
      removeListener = null;
      this.initialized = false;
    },
  },
});
