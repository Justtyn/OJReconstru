import http from '@/services/http';
import type { PageResult } from '@/types/api';
import type { Submission, SubmissionCreateRequest, SubmissionDetail, SubmissionQuery } from '@/types';

export const submissionService = {
  fetchList(params: SubmissionQuery) {
    return http.get<PageResult<Submission>>('/api/submissions', { params });
  },
  fetchDetail(id: string | number) {
    return http.get<SubmissionDetail>(`/api/submissions/${id}`);
  },
  create(payload: SubmissionCreateRequest) {
    return http.post<SubmissionDetail>('/api/submissions', payload);
  },
};
