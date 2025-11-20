import http from '@/services/http';
import type { AdminUser, AdminQuery, AdminUpsertRequest, PageResult } from '@/types';

export const adminUserService = {
  fetchList(params: AdminQuery) {
    return http.get<PageResult<AdminUser>>('/api/admins', { params });
  },
  fetchDetail(id: string) {
    return http.get<AdminUser>(`/api/admins/${id}`);
  },
  create(payload: AdminUpsertRequest) {
    return http.post<AdminUser>('/api/admins', payload);
  },
  update(id: string, payload: AdminUpsertRequest) {
    return http.put<AdminUser>(`/api/admins/${id}`, payload);
  },
  remove(id: string) {
    return http.delete<void>(`/api/admins/${id}`);
  },
};
