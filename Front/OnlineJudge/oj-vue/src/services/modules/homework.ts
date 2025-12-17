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
  fetchDetail(id: string) {
    return http.get<Homework>(`/api/homeworks/${id}`);
  },
  create(payload: HomeworkRequest) {
    return http.post<Homework>('/api/homeworks', payload);
  },
  update(id: string, payload: HomeworkRequest) {
    return http.put<Homework>(`/api/homeworks/${id}`, payload);
  },
  remove(id: string) {
    return http.delete<void>(`/api/homeworks/${id}`);
  },
  fetchProblems(id: string) {
    return http.get<HomeworkProblem[]>(`/api/homeworks/${id}/problems`);
  },
  addProblems(id: string, payload: HomeworkProblemBatchRequest) {
    return http.post<void>(`/api/homeworks/${id}/problems`, payload);
  },
  removeProblem(homeworkId: string, problemId: string) {
    return http.delete<void>(`/api/homeworks/${homeworkId}/problems/${problemId}`);
  },
};
