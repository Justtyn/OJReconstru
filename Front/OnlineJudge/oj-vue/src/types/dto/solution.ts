import type { PageQuery } from '@/types/pagination';

export interface Solution {
  id: string;
  userId: string;
  problemId: string;
  title: string;
  content?: string;
  language?: string;
  createTime?: string;
  updatedAt?: string;
  isActive?: boolean;
}

export interface SolutionRequest {
  problemId?: string | number;
  userId?: string | number;
  authorId?: string | number;
  title: string;
  content: string;
  language?: string;
  isActive?: boolean;
}

export interface SolutionQuery extends PageQuery {
  keyword?: string;
  problemId?: string | number;
  userId?: string | number;
  isActive?: boolean;
}
