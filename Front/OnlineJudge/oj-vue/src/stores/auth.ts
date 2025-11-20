import { defineStore } from 'pinia';
import { message } from 'ant-design-vue';
import type {
  AuthUserVO,
  LoginRequest,
  RegisterRequest,
  VerifyEmailRequest,
  ForgotPasswordSendCodeRequest,
  ForgotPasswordVerifyRequest,
} from '@/types';
import { authService } from '@/services/modules/auth';
import { storage } from '@/utils/storage';

interface AuthState {
  token: string;
  user: AuthUserVO | null;
  initialized: boolean;
  initializing: boolean;
  pendingVerifyUser?: {
    username: string;
    email?: string;
  } | null;
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    token: storage.getToken(),
    user: null,
    initialized: false,
    initializing: false,
    pendingVerifyUser: null,
  }),
  getters: {
    isAuthenticated: (state) => Boolean(state.token && state.user),
    role: (state) => state.user?.role ?? null,
  },
  actions: {
    async login(payload: LoginRequest) {
      const data = await authService.login(payload);
      this.applySession(data);
      return data;
    },
    async register(payload: RegisterRequest) {
      const data = await authService.register(payload);
      if (data) {
        this.applySession(data);
      } else {
        this.pendingVerifyUser = { username: payload.username, email: payload.email };
      }
      return data;
    },
    async verifyEmail(payload: VerifyEmailRequest) {
      const data = await authService.verifyEmail(payload);
      this.applySession(data);
      this.pendingVerifyUser = null;
      return data;
    },
    async fetchProfile() {
      if (!this.token) return null;
      const profile = await authService.fetchProfile();
      this.applySession(profile);
      return profile;
    },
    async logout(options: { silent?: boolean } = {}) {
      if (this.token) {
        try {
          await authService.logout();
        } catch (error) {
          if (!options.silent) {
            console.warn('Logout request failed', error);
          }
        }
      }
      this.resetSession();
    },
    async restore() {
      if (this.initialized || this.initializing) return;
      this.initializing = true;

      const cachedToken = storage.getToken();
      if (cachedToken) {
        this.token = cachedToken;
        try {
          await this.fetchProfile();
        } catch (error) {
          this.resetSession();
          console.warn('Failed to restore session', error);
        }
      }

      this.initialized = true;
      this.initializing = false;
    },
    async sendForgotPasswordCode(payload: ForgotPasswordSendCodeRequest) {
      await authService.sendForgotCode(payload);
      message.success('验证码已发送，请查收邮箱');
    },
    async verifyForgotPassword(payload: ForgotPasswordVerifyRequest) {
      await authService.verifyForgotPassword(payload);
      message.success('密码已更新，请使用新密码登录');
    },
    forceLogout(notice = '登录状态已失效，请重新登录') {
      this.resetSession();
      message.warning(notice);
    },
    applySession(user: AuthUserVO) {
      this.user = user;
      this.token = user.token;
      storage.setToken(user.token);
    },
    resetSession() {
      this.user = null;
      this.token = '';
      storage.clearToken();
      this.pendingVerifyUser = null;
    },
  },
});
