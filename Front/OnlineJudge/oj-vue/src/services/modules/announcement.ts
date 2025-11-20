import http from '@/services/http';
import type { AnnouncementVO, AnnouncementQuery, PageResult } from '@/types';

export const fetchAnnouncements = (params: AnnouncementQuery) =>
  http.get<PageResult<AnnouncementVO>>('/api/announcements', { params });

export const fetchAnnouncementDetail = (id: number) =>
  http.get<AnnouncementVO>(`/api/announcements/${id}`);
