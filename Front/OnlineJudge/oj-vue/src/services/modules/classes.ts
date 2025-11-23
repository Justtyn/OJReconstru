import http from '@/services/http';
import type { Classes, ClassesQuery, ClassesRequest, PageResult } from '@/types';

export const classesService = {
  fetchList(params: ClassesQuery) {
    return http.get<PageResult<Classes>>('/api/classes', { params });
  },
  fetchDetail(id: string | number) {
    return http.get<Classes>(`/api/classes/${id}`);
  },
  create(payload: ClassesRequest) {
    return http.post<Classes>('/api/classes', payload);
  },
  update(id: string | number, payload: ClassesRequest) {
    return http.put<Classes>(`/api/classes/${id}`, payload);
  },
  remove(id: string | number) {
    return http.delete<void>(`/api/classes/${id}`);
  },
};
