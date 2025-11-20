import http from '@/services/http';
import type { PageResult } from '@/types/api';
import type { Student, StudentQuery, StudentUpsertRequest } from '@/types';

export const studentService = {
  fetchList(params: StudentQuery) {
    return http.get<PageResult<Student>>('/api/students', { params });
  },
  fetchDetail(id: string) {
    return http.get<Student>(`/api/students/${id}`);
  },
  create(payload: StudentUpsertRequest) {
    return http.post<Student>('/api/students', payload);
  },
  update(id: string, payload: StudentUpsertRequest) {
    return http.put<Student>(`/api/students/${id}`, payload);
  },
  remove(id: string) {
    return http.delete<void>(`/api/students/${id}`);
  },
};
