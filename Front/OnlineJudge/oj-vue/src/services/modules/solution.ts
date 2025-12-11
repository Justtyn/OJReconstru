import http from '@/services/http';
import type { PageResult } from '@/types/api';
import type { Solution, SolutionQuery, SolutionRequest } from '@/types';

export const solutionService = {
  fetchList(params: SolutionQuery) {
    return http.get<PageResult<Solution>>('/api/solutions', { params });
  },
  fetchDetail(id: string | number) {
    return http.get<Solution>(`/api/solutions/${id}`);
  },
  create(problemId: string | number, payload: SolutionRequest) {
    return http.post<Solution>(`/api/problems/${problemId}/solutions`, payload);
  },
  update(id: string | number, payload: SolutionRequest) {
    return http.put<Solution>(`/api/solutions/${id}`, payload);
  },
  remove(id: string | number) {
    return http.delete<void>(`/api/solutions/${id}`);
  },
};
