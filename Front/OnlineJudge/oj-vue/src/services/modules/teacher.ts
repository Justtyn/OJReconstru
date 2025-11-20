import http from '@/services/http';
import type { Teacher, TeacherQuery, TeacherUpsertRequest, PageResult } from '@/types';

export const teacherService = {
  fetchList(params: TeacherQuery) {
    return http.get<PageResult<Teacher>>('/api/teachers', { params });
  },
  fetchDetail(id: string) {
    return http.get<Teacher>(`/api/teachers/${id}`);
  },
  create(payload: TeacherUpsertRequest) {
    return http.post<Teacher>('/api/teachers', payload);
  },
  update(id: string, payload: TeacherUpsertRequest) {
    return http.put<Teacher>(`/api/teachers/${id}`, payload);
  },
  remove(id: string) {
    return http.delete<void>(`/api/teachers/${id}`);
  },
};
