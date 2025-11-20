import http from '@/services/http';
import type { AnnouncementVO, AnnouncementQuery, PageResult } from '@/types';

// 公共查询：所有角色（含管理员）统一使用 /api/announcements
export const fetchPublicAnnouncements = (params: AnnouncementQuery) =>
  http.get<PageResult<AnnouncementVO>>('/api/announcements', { params });

export const fetchPublicAnnouncementDetail = (id: string) =>
  http.get<AnnouncementVO>(`/api/announcements/${id}`);
