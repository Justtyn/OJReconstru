import http from '@/services/http';
import type { AnnouncementVO, AnnouncementQuery, PageResult, AnnouncementRequest } from '@/types';

export const adminAnnouncementService = {
  fetchList(params: AnnouncementQuery) {
    // 查询接口按文档使用公共查询路径
    return http.get<PageResult<AnnouncementVO>>('/api/announcements', { params });
  },
  fetchDetail(id: string) {
    // 详情同样可通过公共接口获取
    return http.get<AnnouncementVO>(`/api/announcements/${id}`);
  },
  create(payload: AnnouncementRequest) {
    return http.post<AnnouncementVO>('/api/admin/announcements', payload);
  },
  update(id: string, payload: AnnouncementRequest) {
    return http.put<AnnouncementVO>(`/api/admin/announcements/${id}`, payload);
  },
  remove(id: string) {
    return http.delete<void>(`/api/admin/announcements/${id}`);
  },
};
