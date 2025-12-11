import type { PageQuery } from '@/types/pagination';

export interface Discussion {
  id: string;
  userId: string;
  problemId: string;
  title: string;
  content?: string;
  createTime?: string;
  updateTime?: string;
  isActive?: boolean;
}

export interface DiscussionRequest {
  title: string;
  content: string;
  problemId?: string | number;
  userId?: string | number;
  isActive?: boolean;
}

export interface DiscussionQuery extends PageQuery {
  keyword?: string;
  problemId?: string | number;
  userId?: string | number;
  isActive?: boolean;
}

export interface DiscussionComment {
  id: string;
  discussId: string;
  userId: string;
  content: string;
  createTime?: string;
}

export interface DiscussionCommentRequest {
  content: string;
  userId?: string | number;
}
