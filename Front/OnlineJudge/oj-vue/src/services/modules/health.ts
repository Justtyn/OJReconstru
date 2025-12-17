import axios from 'axios';

export type HandshakeResult = {
  ok: boolean;
  name: string;
  endpoint: string;
  method: 'GET';
  status?: number;
  elapsedMs: number;
  checkedAt: number;
  errorMessage?: string;
};

export type HandshakeTarget = {
  name: string;
  endpoint: string;
  method?: 'GET';
  params?: Record<string, any>;
};

const nowMs = () => (typeof performance !== 'undefined' && performance.now ? performance.now() : Date.now());

const healthHttp = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '',
  timeout: 5000,
  validateStatus: () => true,
});

export const healthService = {
  async handshake(name: string, endpoint: string, params?: Record<string, any>): Promise<HandshakeResult> {
    const start = nowMs();
    const checkedAt = Date.now();
    try {
      const resp = await healthHttp.get(endpoint, { params });
      return {
        ok: true,
        name,
        endpoint,
        method: 'GET',
        status: resp.status,
        elapsedMs: Math.max(0, Math.round(nowMs() - start)),
        checkedAt,
      };
    } catch (error: any) {
      const errorMessage =
        error?.code === 'ECONNABORTED'
          ? '请求超时'
          : typeof error?.message === 'string'
            ? error.message
            : '网络错误';
      return {
        ok: false,
        name,
        endpoint,
        method: 'GET',
        elapsedMs: Math.max(0, Math.round(nowMs() - start)),
        checkedAt,
        errorMessage,
      };
    }
  },

  targets(): HandshakeTarget[] {
    return [
      { name: '公告', endpoint: '/api/announcements', params: { page: 1, size: 1 } },
      { name: '鉴权用户', endpoint: '/api/auth/users/me' },
      { name: '学生', endpoint: '/api/students', params: { page: 1, size: 1 } },
      { name: '教师', endpoint: '/api/teachers', params: { page: 1, size: 1 } },
      { name: '班级', endpoint: '/api/classes', params: { page: 1, size: 1 } },
      { name: '班级成员', endpoint: '/api/classes-members', params: { page: 1, size: 1 } },
      { name: '作业', endpoint: '/api/homeworks', params: { page: 1, size: 1 } },
      { name: '题目', endpoint: '/api/problems', params: { page: 1, size: 1 } },
      { name: '提交', endpoint: '/api/submissions', params: { page: 1, size: 1 } },
      { name: '讨论', endpoint: '/api/discussions', params: { page: 1, size: 1 } },
      { name: '题解', endpoint: '/api/solutions', params: { page: 1, size: 1 } },
      { name: '登录日志', endpoint: '/api/login-logs', params: { page: 1, size: 1 } },
      { name: '统计汇总', endpoint: '/api/analytics/summary' },
      { name: '统计-提交状态', endpoint: '/api/analytics/submissions/status' },
      { name: '统计-题目状态', endpoint: '/api/analytics/problems/status' },
      { name: '统计-题解状态', endpoint: '/api/analytics/solutions/status' },
      { name: '统计-讨论状态', endpoint: '/api/analytics/discussions/status' },
      { name: '统计-登录状态', endpoint: '/api/analytics/logins/status' },
    ];
  },

  async checkAll(): Promise<HandshakeResult[]> {
    const targets = healthService.targets();
    const results = await Promise.all(targets.map((t) => healthService.handshake(t.name, t.endpoint, t.params)));
    return results.sort((a, b) => a.name.localeCompare(b.name));
  },
};
