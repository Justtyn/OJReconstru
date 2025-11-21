import http from '@/services/http';
import type { PageResult } from '@/types/api';
import type { LoginLog, LoginLogQuery } from '@/types';

export const loginLogService = {
  fetchList(params: LoginLogQuery) {
    return http.get<PageResult<LoginLog>>('/api/login-logs', { params });
  },
  fetchDetail(id: string) {
    return http.get<LoginLog>(`/api/login-logs/${id}`);
  },
  remove(id: string) {
    return http.delete<void>(`/api/login-logs/${id}`);
  },
};
