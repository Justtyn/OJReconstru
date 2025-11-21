import http from '@/services/http';
import type { PageResult } from '@/types/api';
import type {
  Problem,
  ProblemCase,
  ProblemCaseQuery,
  ProblemCaseUpsertRequest,
  ProblemQuery,
  ProblemUpsertRequest,
} from '@/types';

export const problemService = {
  fetchList(params: ProblemQuery) {
    return http.get<PageResult<Problem>>('/api/problems', { params });
  },
  fetchDetail(id: string) {
    return http.get<Problem>(`/api/problems/${id}`);
  },
  create(payload: ProblemUpsertRequest) {
    return http.post<Problem>('/api/admin/problems', payload);
  },
  update(id: string, payload: ProblemUpsertRequest) {
    return http.put<Problem>(`/api/admin/problems/${id}`, payload);
  },
  remove(id: string) {
    return http.delete<void>(`/api/admin/problems/${id}`);
  },
};

export const problemCaseService = {
  fetchList(problemId: string, params?: Partial<Omit<ProblemCaseQuery, 'problemId'>>) {
    return http.get<ProblemCase[]>(`/api/admin/problems/${problemId}/testcases`, { params });
  },
  fetchDetail(caseId: string) {
    return http.get<ProblemCase>(`/api/admin/problem-testcases/${caseId}`);
  },
  create(problemId: string, payload: ProblemCaseUpsertRequest) {
    return http.post<ProblemCase>(`/api/admin/problems/${problemId}/testcases`, payload);
  },
  update(caseId: string, payload: ProblemCaseUpsertRequest) {
    return http.put<ProblemCase>(`/api/admin/problem-testcases/${caseId}`, payload);
  },
  remove(caseId: string) {
    return http.delete<void>(`/api/admin/problem-testcases/${caseId}`);
  },
};
