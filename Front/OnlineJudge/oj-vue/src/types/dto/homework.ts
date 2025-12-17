import type { PageQuery } from '@/types/pagination';

export interface Homework {
  id: string;
  title: string;
  classId: string;
  startTime?: string;
  endTime?: string;
  description?: string;
  isActive?: boolean;
  problemIds?: string[];
  createdAt?: string;
  updatedAt?: string;
}

export interface HomeworkRequest {
  title: string;
  classId: string;
  startTime?: string;
  endTime?: string;
  description?: string;
  isActive?: boolean;
  problemIds?: string[];
}

export interface HomeworkQuery extends PageQuery {
  classId?: string;
  keyword?: string;
  activeOnly?: boolean;
}

export interface HomeworkProblem {
  id: string;
  homeworkId?: string;
  problemId?: string;
  name?: string;
  difficulty?: string;
  isActive?: boolean;
  acCount?: number;
  submitCount?: number;
  createdAt?: string;
  createTime?: string;
  updatedAt?: string;
}

export interface HomeworkProblemBatchRequest {
  problemIds: string[];
}
