import http from '@/services/http';
import type { PageResult } from '@/types/api';
import type {
  Discussion,
  DiscussionComment,
  DiscussionCommentRequest,
  DiscussionQuery,
  DiscussionRequest,
} from '@/types';

export const discussionService = {
  fetchList(params: DiscussionQuery) {
    return http.get<PageResult<Discussion>>('/api/discussions', { params });
  },
  fetchDetail(id: string | number) {
    return http.get<Discussion>(`/api/discussions/${id}`);
  },
  create(payload: DiscussionRequest) {
    return http.post<Discussion>('/api/discussions', payload);
  },
  update(id: string | number, payload: DiscussionRequest) {
    return http.put<Discussion>(`/api/discussions/${id}`, payload);
  },
  remove(id: string | number) {
    return http.delete<void>(`/api/discussions/${id}`);
  },
  fetchComments(id: string | number) {
    return http.get<DiscussionComment[]>(`/api/discussions/${id}/comments`);
  },
  createComment(id: string | number, payload: DiscussionCommentRequest) {
    return http.post<DiscussionComment>(`/api/discussions/${id}/comments`, payload);
  },
  removeComment(commentId: string | number) {
    return http.delete<void>(`/api/discussions/comments/${commentId}`);
  },
};
