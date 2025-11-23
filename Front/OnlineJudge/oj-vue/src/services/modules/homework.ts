import http from '@/services/http';
import type { Homework, HomeworkQuery, HomeworkRequest, PageResult } from '@/types';

export const homeworkService = {
  fetchList(params: HomeworkQuery) {
    return http.get<PageResult<Homework>>('/api/homeworks', { params });
  },
  fetchDetail(id: string | number) {
    return http.get<Homework>(`/api/homeworks/${id}`);
  },
  create(payload: HomeworkRequest) {
    return http.post<Homework>('/api/homeworks', payload);
  },
  update(id: string | number, payload: HomeworkRequest) {
    return http.put<Homework>(`/api/homeworks/${id}`, payload);
  },
  remove(id: string | number) {
    return http.delete<void>(`/api/homeworks/${id}`);
  },
};
