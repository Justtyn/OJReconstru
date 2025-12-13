import type { PageQuery } from '@/types/pagination';

export interface Homework {
  id: string;
  title: string;
  classId: string;
  startTime?: string;
  endTime?: string;
  description?: string;
  isActive?: boolean;
  problemIds?: number[];
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
  problemIds?: number[];
}

export interface HomeworkQuery extends PageQuery {
  classId?: number;
  keyword?: string;
  activeOnly?: boolean;
}

export interface HomeworkProblem {
  id: string;
  homeworkId?: string;
  problemId?: string | number;
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
  problemIds: Array<string | number>;
}
