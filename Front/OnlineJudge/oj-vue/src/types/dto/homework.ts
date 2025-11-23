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
