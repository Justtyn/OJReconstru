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
  fetchDetail(id: string) {
    return http.get<Discussion>(`/api/discussions/${id}`);
  },
  create(payload: DiscussionRequest) {
    return http.post<Discussion>('/api/discussions', payload);
  },
  update(id: string, payload: DiscussionRequest) {
    return http.put<Discussion>(`/api/discussions/${id}`, payload);
  },
  remove(id: string) {
    return http.delete<void>(`/api/discussions/${id}`);
  },
  fetchComments(id: string) {
    return http.get<DiscussionComment[]>(`/api/discussions/${id}/comments`);
  },
  createComment(id: string, payload: DiscussionCommentRequest) {
    return http.post<DiscussionComment>(`/api/discussions/${id}/comments`, payload);
  },
  removeComment(commentId: string) {
    return http.delete<void>(`/api/discussions/comments/${commentId}`);
  },
};
