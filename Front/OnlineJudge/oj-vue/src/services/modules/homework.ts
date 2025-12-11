import http from '@/services/http';
import type {
  Homework,
  HomeworkProblem,
  HomeworkProblemBatchRequest,
  HomeworkQuery,
  HomeworkRequest,
  PageResult,
} from '@/types';

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
  fetchProblems(id: string | number) {
    return http.get<HomeworkProblem[]>(`/api/homeworks/${id}/problems`);
  },
  addProblems(id: string | number, payload: HomeworkProblemBatchRequest) {
    return http.post<void>(`/api/homeworks/${id}/problems`, payload);
  },
  removeProblem(homeworkId: string | number, problemId: string | number) {
    return http.delete<void>(`/api/homeworks/${homeworkId}/problems/${problemId}`);
  },
};
